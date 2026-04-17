package com.localaichat.domain.model

/**
 * The final rendered string ready to be sent to a specific LLM backend.
 */
data class RenderedPrompt(
    val text: String,
)
