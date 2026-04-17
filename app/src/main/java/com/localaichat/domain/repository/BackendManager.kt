package com.localaichat.domain.repository

import com.localaichat.domain.model.BackendOption
import com.localaichat.domain.model.BackendType
import kotlinx.coroutines.flow.Flow

/**
 * Manager for inference backends.
 *
 * Allows observing available backends, their availability status, and selecting
 * the active backend.
 */
interface BackendManager {
    /**
     * Observe the list of all supported backends and their current status.
     */
    fun observeAvailableBackends(): Flow<List<BackendOption>>

    /**
     * Observe the currently selected backend type.
     */
    fun observeSelectedBackendType(): Flow<BackendType>

    /**
     * Change the active backend.
     */
    suspend fun selectBackend(type: BackendType)
}
