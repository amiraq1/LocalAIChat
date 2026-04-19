package com.localaichat.data.repository

import com.localaichat.domain.model.ModelCompatibility
import com.localaichat.domain.model.ModelManagerState
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.ModelStatus
import com.localaichat.domain.repository.ModelCompatibilityChecker
import com.localaichat.domain.repository.ModelManager
import com.localaichat.domain.repository.ModelRepository
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ModelManagerImpl(
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository,
    private val compatibilityChecker: ModelCompatibilityChecker,
) : ModelManager {
    private val statusOverrides = MutableStateFlow<Map<String, ModelStatus>>(emptyMap())
    private val activeModelId = MutableStateFlow<String?>(null)
    private val prepareMutex = Mutex()

    override fun observeAvailableModels(): Flow<List<ModelOption>> = combine(
        modelRepository.observeAvailableModels(),
        statusOverrides,
        settingsRepository.observeBackendType(),
    ) { models, statusMap, backendType ->
        models.map { model ->
            model.copy(
                status = statusMap[model.id] ?: model.status,
                compatibility = compatibilityChecker.checkCompatibility(model, backendType)
            )
        }
    }

    override fun observeSelectedModelId(): Flow<String> = modelRepository.observeSelectedModelId()

    override fun observeManagerState(): Flow<ModelManagerState> = combine(
        observeSelectedModelId(),
        observeAvailableModels(),
        activeModelId,
    ) { selectedModelId, models, activeId ->
        val selectedModel = models.firstOrNull { it.id == selectedModelId }
        val activeModel = models.firstOrNull { it.id == activeId }
        val selectedStatus = selectedModel?.status ?: ModelStatus.NotLoaded
        ModelManagerState(
            selectedModelId = selectedModelId,
            selectedModelName = selectedModel?.name,
            activeModelId = activeId,
            activeModelName = activeModel?.name,
            selectedModelStatus = selectedStatus,
            selectedModelFailureMessage = (selectedStatus as? ModelStatus.Failed)?.reason,
            selectedModelAvailabilityMessage = availabilityMessageFor(selectedModel, selectedStatus),
        )
    }

    override suspend fun getSelectedModelOrNull(): ModelOption? {
        val selectedId = modelRepository.observeSelectedModelId().first()
        if (selectedId.isBlank()) {
            return null
        }
        return observeAvailableModels().first().firstOrNull { it.id == selectedId }
    }

    override suspend fun getSelectedModel(): ModelOption {
        return getSelectedModelOrNull() ?: error("No local model is currently selected.")
    }

    override suspend fun selectModel(modelId: String) {
        modelRepository.selectModel(modelId)
        if (activeModelId.value != modelId) {
            activeModelId.value = null
        }
    }

    override suspend fun prepareSelectedModel(): ModelOption = prepareMutex.withLock {
        val selectedModel = getSelectedModel()
        ensureCompatible(selectedModel)

        if (selectedModel.status == ModelStatus.Ready && activeModelId.value == selectedModel.id) {
            return selectedModel
        }

        if (selectedModel.status == ModelStatus.Ready) {
            activeModelId.value = selectedModel.id
        }
        getSelectedModel()
    }

    override suspend fun updateModelStatus(modelId: String, status: ModelStatus) {
        setStatus(modelId, status)
        when (status) {
            ModelStatus.Ready -> activeModelId.value = modelId
            ModelStatus.NotLoaded,
            is ModelStatus.Loading,
            is ModelStatus.Initializing,
            is ModelStatus.Failed -> if (activeModelId.value == modelId) {
                activeModelId.value = null
            }
        }
    }

    override suspend fun markModelFailed(modelId: String, reason: String) {
        updateModelStatus(modelId, ModelStatus.Failed(reason))
    }

    override suspend fun resetModel(modelId: String) {
        updateModelStatus(modelId, ModelStatus.NotLoaded)
    }

    private fun setStatus(modelId: String, status: ModelStatus) {
        statusOverrides.update { current ->
            current + (modelId to status)
        }
    }

    private suspend fun ensureCompatible(selectedModel: ModelOption) {
        if (selectedModel.compatibility is ModelCompatibility.Incompatible) {
            val reason = selectedModel.compatibility.reason
            markModelFailed(selectedModel.id, reason)
            error(reason)
        }
    }

    private fun availabilityMessageFor(
        selectedModel: ModelOption?,
        selectedStatus: ModelStatus,
    ): String {
        val model = selectedModel ?: return "Select a local model to start chatting."

        if (selectedModel.compatibility is ModelCompatibility.Incompatible) {
            return selectedModel.compatibility.reason
        }

        return when (selectedStatus) {
            ModelStatus.NotLoaded -> if (!model.isInstalled) {
                "${model.name} is not installed on this device."
            } else {
                "${model.name} is selected but not loaded."
            }
            is ModelStatus.Loading -> "${model.name} is loading (${selectedStatus.progressPercent}%)..."
            is ModelStatus.Initializing -> "${model.name} is initializing (${selectedStatus.progressPercent}%)..."
            ModelStatus.Ready -> "${model.name} is ready."
            is ModelStatus.Failed -> selectedStatus.reason
        }
    }
}
