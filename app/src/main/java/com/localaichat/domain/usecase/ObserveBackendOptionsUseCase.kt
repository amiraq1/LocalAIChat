package com.localaichat.domain.usecase

import com.localaichat.domain.model.BackendOption
import com.localaichat.domain.repository.BackendManager
import kotlinx.coroutines.flow.Flow

/**
 * Observes the current list of available backends and their status.
 */
class ObserveBackendOptionsUseCase(
    private val backendManager: BackendManager,
) {
    operator fun invoke(): Flow<List<BackendOption>> = backendManager.observeAvailableBackends()
}
