package com.localaichat.domain.usecase

import com.localaichat.domain.model.SettingsState
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.ModelManager
import com.localaichat.domain.repository.ModelRepository
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.combine

class ObserveSettingsUseCase(
    private val settingsRepository: SettingsRepository,
    private val modelRepository: ModelRepository,
    private val backendManager: BackendManager,
    private val modelManager: ModelManager,
) {
    operator fun invoke() = combine(
        settingsRepository.observeGenerationConfig(),
        modelRepository.observeSelectedModelId(),
        settingsRepository.observeBackendType(),
        backendManager.observeAvailableBackends(),
        modelManager.observeManagerState(),
    ) { config, selectedModelId, backend, availableBackends, modelState ->
        SettingsState(
            generationConfig = config,
            selectedModelId = selectedModelId,
            selectedBackend = backend,
            availableBackends = availableBackends,
            selectedModelCompatibility = modelState.selectedModelAvailabilityMessage,
        )
    }
}
