package com.localaichat.data.repository

import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.LlmEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalLlmEngine : LlmEngine {
    override fun streamReply(
        renderedPrompt: RenderedPrompt,
        config: GenerationConfig,
        model: ModelOption,
    ): Flow<String> = flow {
        val synthesized = buildString {
            append("Running ")
            append(model.name)
            append(" with temperature ")
            append(String.format("%.2f", config.temperature))
            append(" and max tokens ")
            append(config.maxTokens)
            append(".\n\n")
            append("Rendered prompt length: ")
            append(renderedPrompt.text.length)
            append(" characters.")
            append("\n\n")
            append("This response is streamed from a swap-ready engine interface so you can replace it with llama.cpp, MediaPipe, ONNX Runtime, or another local backend later.")
        }

        synthesized.chunked(18).forEach { chunk ->
            delay(55)
            emit(chunk)
        }
    }
}
