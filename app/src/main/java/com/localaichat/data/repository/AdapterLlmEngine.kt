package com.localaichat.data.repository

import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.InferenceAdapter
import com.localaichat.domain.repository.LlmEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
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

        return adapter.streamInference(request).mapNotNull { event ->
            when (event) {
                is InferenceEvent.Token -> event.text
                is InferenceEvent.Failed -> "[Error: ${event.userMessage}]"
                else -> null
            }
        }
    }
}
