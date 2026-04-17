package com.localaichat.domain.usecase

import com.localaichat.domain.model.BackendType
import com.localaichat.domain.repository.BackendManager

/**
 * Changes the selected inference backend.
 */
class SelectBackendUseCase(
    private val backendManager: BackendManager,
) {
    suspend operator fun invoke(type: BackendType) {
        backendManager.selectBackend(type)
    }
}
