package com.localaichat.data.backend.real

import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow

/**
 * Concrete session boundary for specific native runtimes.
 */
interface RealBackendSession {
    /**
     * Initialize the model and emit progress events.
     */
    fun initializeModel(model: InferenceModelHandle): Flow<LocalModelLoadEvent>

    /**
     * Execute inference and emit streaming events.
     */
    fun executeInference(request: InferenceRequest): Flow<InferenceEvent>

    /**
     * Cancel active inference.
     */
    suspend fun cancel(requestId: String)

    /**
     * Release native resources.
     */
    suspend fun teardown()
}
