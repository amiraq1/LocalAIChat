package com.localaichat.domain.repository

import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import kotlinx.coroutines.flow.Flow

interface LlmEngine {
    fun streamReply(
        renderedPrompt: RenderedPrompt,
        config: GenerationConfig,
        model: ModelOption,
    ): Flow<String>
}
