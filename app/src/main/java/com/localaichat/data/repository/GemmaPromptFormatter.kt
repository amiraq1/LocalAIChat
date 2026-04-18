package com.localaichat.data.repository

import com.localaichat.domain.model.ChatRole
import com.localaichat.domain.model.PromptInput
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.PromptFormatter

/**
 * Formatter for Google's Gemma models.
 */
class GemmaPromptFormatter : PromptFormatter {
    override fun format(input: PromptInput): RenderedPrompt = RenderedPrompt(
        text = buildString {
            if (!input.systemPrompt.isNullOrBlank()) {
                append("<start_of_turn>user\n")
                append(input.systemPrompt)
                append("\n<end_of_turn>\n")
            }

            input.history.forEach { message ->
                val role = when (message.role) {
                    ChatRole.USER -> "user"
                    ChatRole.ASSISTANT -> "model"
                }
                append("<start_of_turn>$role\n")
                append(message.content)
                append("<end_of_turn>\n")
            }
            
            append("<start_of_turn>model\n")
        }
    )
}
