package com.localaichat.domain.model

/**
 * Encapsulates all data needed to render a prompt for an LLM.
 */
data class PromptInput(
    val history: List<ChatMessage>,
    val systemPrompt: String? = null,
)
