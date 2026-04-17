package com.localaichat.domain.repository

import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow

/**
 * Low-level boundary for a specific on-device inference runtime.
 */
interface InferenceAdapter : BackendCapabilityNegotiator {
    /**
     * Prepare a model for inference and emit progress.
     */
    fun initializeModel(model: InferenceModelHandle): Flow<LocalModelLoadEvent>

    /**
     * Release resources for a model.
     */
    suspend fun teardownModel(modelId: String)

    /**
     * Submit a prompt and receive streaming events.
     */
    fun streamInference(request: InferenceRequest): Flow<InferenceEvent>

    /**
     * Cancel active inference.
     */
    suspend fun cancelInference(requestId: String)
}
