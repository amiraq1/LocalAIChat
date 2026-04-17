package com.localaichat.data.repository

import com.localaichat.domain.model.ChatRole
import com.localaichat.domain.model.PromptInput
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.PromptFormatter

/**
 * Formatter for Meta's Llama 3 models.
 */
class Llama3PromptFormatter : PromptFormatter {
    override fun format(input: PromptInput): RenderedPrompt = RenderedPrompt(
        text = buildString {
            append("<|begin_of_text|>")
            
            if (!input.systemPrompt.isNullOrBlank()) {
                append("<|start_header_id|>system<|end_header_id|>\n\n")
                append(input.systemPrompt)
                append("<|eot_id|>")
            }

            input.history.forEach { message ->
                val role = when (message.role) {
                    ChatRole.USER -> "user"
                    ChatRole.ASSISTANT -> "assistant"
                    ChatRole.SYSTEM -> "system"
                }
                append("<|start_header_id|>$role<|end_header_id|>\n\n")
                append(message.content)
                append("<|eot_id|>")
            }
            
            append("<|start_header_id|>assistant<|end_header_id|>\n\n")
        }
    )
}
