package com.localaichat.data.backend.mediapipe

import com.localaichat.data.backend.real.RealBackendPromptExecutor
import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * MediaPipe-specific prompt executor.
 */
class MediaPipePromptExecutor : RealBackendPromptExecutor {
    
    override fun execute(request: InferenceRequest): Flow<InferenceEvent> = flow {
        // TODO: Map the rendered prompt text to the native caller
        // val promptText = request.prompt.text
        
        // TODO: Call generateResponseAsync() with a result listener
        // llmInference.generateResponseAsync(promptText)

        emit(InferenceEvent.Started(request.requestId))
        
        val response = "This is a placeholder response from the MediaPipe execution stub. " +
                      "Rendered prompt length: ${request.prompt.text.length} chars."
        
        response.split(" ").forEach { word ->
            delay(50)
            emit(InferenceEvent.Token(request.requestId, "$word "))
        }
        
        emit(InferenceEvent.Completed(request.requestId))
    }

    override suspend fun cancel(requestId: String) {
        // TODO: Interrupt the active MediaPipe generation job.
    }
}
