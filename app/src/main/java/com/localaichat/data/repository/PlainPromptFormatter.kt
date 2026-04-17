package com.localaichat.data.repository

import com.localaichat.domain.model.ChatRole
import com.localaichat.domain.model.PromptInput
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.PromptFormatter

/**
 * Basic formatter that simply concatenates history as text.
 */
class PlainPromptFormatter : PromptFormatter {
    override fun format(input: PromptInput): RenderedPrompt = RenderedPrompt(
        text = buildString {
            if (!input.systemPrompt.isNullOrBlank()) {
                append("System: ")
                append(input.systemPrompt)
                append("\n\n")
            }

            input.history.forEach { message ->
                val role = when (message.role) {
                    ChatRole.USER -> "User"
                    ChatRole.ASSISTANT -> "Assistant"
                    ChatRole.SYSTEM -> "System"
                }
                append("$role: ")
                append(message.content)
                append("\n")
            }
            
            append("Assistant: ")
        }
    )
}
