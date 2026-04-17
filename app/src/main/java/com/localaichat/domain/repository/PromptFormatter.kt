package com.localaichat.domain.repository

import com.localaichat.domain.model.PromptInput
import com.localaichat.domain.model.RenderedPrompt

/**
 * Strategy for formatting [PromptInput] into a [RenderedPrompt]
 * compatible with specific LLM architectures (e.g., Gemma, Llama 3).
 */
interface PromptFormatter {
    /**
     * Formats the input into a rendered prompt string.
     */
    fun format(input: PromptInput): RenderedPrompt
}
