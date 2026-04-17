package com.localaichat.data.backend.real

import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceRequest
import kotlinx.coroutines.flow.Flow

/**
 * Executes prompt requests against an initialized backend runtime.
 */
interface RealBackendPromptExecutor {
    fun execute(request: InferenceRequest): Flow<InferenceEvent>
    suspend fun cancel(requestId: String)
}
