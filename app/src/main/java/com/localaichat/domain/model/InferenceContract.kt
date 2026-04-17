package com.localaichat.domain.model

/**
 * Handle to a local model file with format information.
 */
data class InferenceModelHandle(
    val id: String,
    val localPath: String,
    val format: String,
)

/**
 * Request payload for a specific inference operation.
 */
data class InferenceRequest(
    val requestId: String,
    val model: InferenceModelHandle,
    val prompt: RenderedPrompt,
    val config: GenerationConfig,
)

/**
 * Events emitted during the inference process.
 */
sealed interface InferenceEvent {
    data class Started(val requestId: String) : InferenceEvent
    data class Token(val requestId: String, val text: String) : InferenceEvent
    data class Completed(val requestId: String) : InferenceEvent
    data class Cancelled(val requestId: String) : InferenceEvent
    data class Failed(
        val requestId: String, 
        val userMessage: String, 
        val cause: Throwable? = null
    ) : InferenceEvent
}
