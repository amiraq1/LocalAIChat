package com.localaichat.domain.usecase

import com.localaichat.domain.model.BackendType
import com.localaichat.domain.repository.BackendManager
import kotlinx.coroutines.flow.Flow

/**
 * Observes the currently selected backend type.
 */
class ObserveBackendSelectionUseCase(
    private val backendManager: BackendManager,
) {
    operator fun invoke(): Flow<BackendType> = backendManager.observeSelectedBackendType()
}
