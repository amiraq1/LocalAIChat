package com.localaichat.data.repository

import com.localaichat.domain.model.LocalModel
import com.localaichat.domain.model.LocalModelInstallEvent
import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.LocalModelOperationStage
import com.localaichat.domain.model.LocalModelRegistryState
import com.localaichat.domain.model.LocalModelState
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.ModelStatus
import com.localaichat.domain.repository.LocalModelInstallationWorkflow
import com.localaichat.domain.repository.LocalModelLoadingWorkflow
import com.localaichat.domain.repository.LocalModelRegistry
import com.localaichat.domain.repository.ModelManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalModelRegistryImpl(
    private val modelManager: ModelManager,
    private val installationWorkflow: LocalModelInstallationWorkflow,
    private val loadingWorkflow: LocalModelLoadingWorkflow,
) : LocalModelRegistry {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val installedOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val lifecycleOverrides = MutableStateFlow<Map<String, LocalModelState>>(emptyMap())
    private val installJobs = mutableMapOf<String, Job>()
    private val loadJobs = mutableMapOf<String, Job>()

    override fun observeRegistryState(): Flow<LocalModelRegistryState> = combine(
        modelManager.observeAvailableModels(),
        modelManager.observeManagerState(),
        installedOverrides,
        lifecycleOverrides,
    ) { models, managerState, installMap, lifecycleMap ->
        LocalModelRegistryState(
            models = models.map { model ->
                model.asLocalModel(
                    selectedModelId = managerState.selectedModelId,
                    activeModelId = managerState.activeModelId,
                    installedOverride = installMap[model.id],
                    lifecycleOverride = lifecycleMap[model.id],
                )
            },
            selectedModelId = managerState.selectedModelId,
            activeModelId = managerState.activeModelId,
        )
    }

    override suspend fun selectModel(modelId: String) {
        modelManager.selectModel(modelId)
    }

    override suspend fun installModel(modelId: String) {
        if (installJobs[modelId]?.isActive == true) {
            return
        }
        val model = findModel(modelId)
        installJobs[modelId] = scope.launch {
            try {
                installationWorkflow.install(model).collect { event ->
                    when (event) {
                        is LocalModelInstallEvent.Downloading -> {
                            setLifecycle(modelId, LocalModelState.Processing(LocalModelOperationStage.DOWNLOADING, event.progressPercent))
                        }

                        is LocalModelInstallEvent.Installing -> {
                            setLifecycle(modelId, LocalModelState.Processing(LocalModelOperationStage.INSTALLING, event.progressPercent))
                        }

                        LocalModelInstallEvent.Installed -> {
                            setInstalled(modelId, true)
                            clearLifecycle(modelId)
                        }

                        is LocalModelInstallEvent.Failed -> {
                            setLifecycle(modelId, LocalModelState.Failed(event.userMessage))
                        }
                    }
                }
            } catch (_: CancellationException) {
                setLifecycle(modelId, LocalModelState.Failed("Installation canceled."))
            } finally {
                installJobs.remove(modelId)
            }
        }
    }

    override suspend fun cancelInstall(modelId: String) {
        installJobs.remove(modelId)?.cancel()
    }

    override suspend fun loadModel(modelId: String) {
        if (loadJobs[modelId]?.isActive == true) {
            return
        }
        val model = findModel(modelId)
        val isInstalled = installedOverrides.value[modelId] ?: model.isInstalled
        if (!isInstalled) {
            setLifecycle(modelId, LocalModelState.Failed("Install the model before loading it."))
            return
        }

        loadJobs[modelId] = scope.launch {
            try {
                modelManager.selectModel(modelId)
                modelManager.updateModelStatus(modelId, ModelStatus.Loading(0))
                loadingWorkflow.load(model).collect { event ->
                    when (event) {
                        is LocalModelLoadEvent.Loading -> {
                            setLifecycle(modelId, LocalModelState.Processing(LocalModelOperationStage.LOADING_INTO_MEMORY, event.progressPercent))
                            modelManager.updateModelStatus(modelId, ModelStatus.Loading(event.progressPercent))
                        }

                        is LocalModelLoadEvent.Initializing -> {
                            setLifecycle(modelId, LocalModelState.Processing(LocalModelOperationStage.INITIALIZING, event.progressPercent))
                            modelManager.updateModelStatus(modelId, ModelStatus.Initializing(event.progressPercent))
                        }

                        LocalModelLoadEvent.Ready -> {
                            modelManager.updateModelStatus(modelId, ModelStatus.Ready)
                            clearLifecycle(modelId)
                        }

                        is LocalModelLoadEvent.Failed -> {
                            setLifecycle(modelId, LocalModelState.Failed(event.userMessage))
                            modelManager.markModelFailed(modelId, event.userMessage)
                        }
                    }
                }
            } catch (_: CancellationException) {
                setLifecycle(modelId, LocalModelState.Failed("Loading canceled."))
                modelManager.resetModel(modelId)
            } finally {
                loadJobs.remove(modelId)
            }
        }
    }

    override suspend fun cancelLoad(modelId: String) {
        loadJobs.remove(modelId)?.cancel()
    }

    override suspend fun unloadModel(modelId: String) {
        loadJobs.remove(modelId)?.cancel()
        modelManager.resetModel(modelId)
        clearLifecycle(modelId)
    }

    private suspend fun findModel(modelId: String): ModelOption =
        modelManager.observeAvailableModels().first().first { it.id == modelId }

    private fun setInstalled(modelId: String, isInstalled: Boolean) {
        installedOverrides.update { current ->
            current + (modelId to isInstalled)
        }
    }

    private fun setLifecycle(modelId: String, state: LocalModelState) {
        lifecycleOverrides.update { current ->
            current + (modelId to state)
        }
    }

    private fun clearLifecycle(modelId: String) {
        lifecycleOverrides.update { current ->
            current - modelId
        }
    }
}

private fun ModelOption.asLocalModel(
    selectedModelId: String,
    activeModelId: String?,
    installedOverride: Boolean?,
    lifecycleOverride: LocalModelState?,
): LocalModel = LocalModel(
    id = id,
    displayName = name,
    description = description,
    sizeBytes = sizeBytes,
    localPath = localPath,
    isInstalled = installedOverride ?: isInstalled,
    state = lifecycleOverride ?: when {
        !(installedOverride ?: isInstalled) -> LocalModelState.NotInstalled
        status == ModelStatus.NotLoaded -> LocalModelState.Installed
        status is ModelStatus.Loading -> LocalModelState.Processing(
            stage = LocalModelOperationStage.LOADING_INTO_MEMORY,
            progressPercent = status.progressPercent,
        )
        status is ModelStatus.Initializing -> LocalModelState.Processing(
            stage = LocalModelOperationStage.INITIALIZING,
            progressPercent = status.progressPercent,
        )
        status == ModelStatus.Ready -> LocalModelState.Ready
        status is ModelStatus.Failed -> LocalModelState.Failed(status.reason)
        else -> LocalModelState.Installed
    },
    isSelected = selectedModelId == id,
    isActive = activeModelId == id,
)
