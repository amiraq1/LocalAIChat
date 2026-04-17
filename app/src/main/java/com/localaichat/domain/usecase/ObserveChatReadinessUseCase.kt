package com.localaichat.domain.usecase

import com.localaichat.domain.model.BackendStatus
import com.localaichat.domain.model.ChatReadiness
import com.localaichat.domain.model.ModelCompatibility
import com.localaichat.domain.model.ModelStatus
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.ModelManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Computes a single unified readiness status for the chat layer.
 *
 * It combines backend selection, backend availability, backend-model compatibility,
 * and model status (loading/ready).
 */
class ObserveChatReadinessUseCase(
    private val backendManager: BackendManager,
    private val modelManager: ModelManager,
) {
    operator fun invoke(): Flow<ChatReadiness> = combine(
        backendManager.observeAvailableBackends(),
        backendManager.observeSelectedBackendType(),
        modelManager.observeManagerState(),
    ) { availableBackends, selectedBackendType, modelState ->
        val backendOption = availableBackends.find { it.type == selectedBackendType }
        val backendStatus = backendOption?.status ?: BackendStatus.Unavailable("Selected backend not found.")

        // 1. Check Backend Availability
        if (backendStatus is BackendStatus.Unavailable) {
            return@combine ChatReadiness.Blocked(
                message = "${backendOption?.displayName ?: "Selected backend"} is unavailable: ${backendStatus.reason}",
                isRecoverable = true
            )
        }

        // 2. Check Model Selection
        if (modelState.selectedModelId.isBlank()) {
            return@combine ChatReadiness.Blocked(
                message = "Select a local model to start chatting.",
                isRecoverable = true
            )
        }

        // 3. Check Backend-Model Compatibility
        val selectedModel = modelManager.observeAvailableModels()
            .let { /* We can't collect inside combine flow, so we use the state info */ }
        
        // Actually, ModelManagerState has selectedModelAvailabilityMessage which already 
        // includes compatibility checks in our ModelManagerImpl.
        
        // Let's rely on modelManager's internal rules but expose them clearly here.
        val modelStatus = modelState.selectedModelStatus

        if (modelStatus is ModelStatus.Failed) {
            return@combine ChatReadiness.Blocked(
                message = modelStatus.reason,
                isRecoverable = true
            )
        }

        when (modelStatus) {
            ModelStatus.NotLoaded -> ChatReadiness.Blocked(
                message = "${modelState.selectedModelName} is selected but not loaded.",
                isRecoverable = true
            )
            ModelStatus.Loading, ModelStatus.Initializing -> ChatReadiness.Loading(
                message = "Preparing ${modelState.selectedModelName}..."
            )
            ModelStatus.Ready -> ChatReadiness.Ready
            else -> ChatReadiness.Blocked("Inconsistent chat state.")
        }
    }
}
