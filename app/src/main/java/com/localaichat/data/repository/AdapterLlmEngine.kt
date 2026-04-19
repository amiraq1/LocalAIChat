package com.localaichat.data.repository

import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.InferenceAdapter
import com.localaichat.domain.repository.LlmEngine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.util.UUID

/**
 * A general [LlmEngine] that delegates to an [InferenceAdapter].
 *
 * This removes the need for backend-specific engine classes like MediaPipeLlmEngine.
 */
class AdapterLlmEngine(
    private val adapter: InferenceAdapter,
) : LlmEngine {

    override fun streamReply(
        renderedPrompt: RenderedPrompt,
        config: GenerationConfig,
        model: ModelOption,
    ): Flow<String> {
        val request = InferenceRequest(
            requestId = UUID.randomUUID().toString(),
            prompt = renderedPrompt,
            config = config,
            model = InferenceModelHandle(
                id = model.id,
                localPath = model.localPath,
                format = "bin" // TODO: map this from model metadata
            )
        )

        return adapter.streamInference(request).transform { event ->
            when (event) {
                is InferenceEvent.Token -> emit(event.text)
                is InferenceEvent.Failed -> throw IllegalStateException(event.userMessage, event.cause)
                is InferenceEvent.Cancelled -> {
                    throw CancellationException("Inference cancelled for request ${event.requestId}.")
                }
                else -> Unit
            }
        }
    }
}
