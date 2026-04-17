package com.localaichat.domain.model

/**
 * Unified readiness status for the chat layer.
 */
sealed interface ChatReadiness {
    /**
     * Chat is ready to accept user input.
     */
    object Ready : ChatReadiness

    /**
     * Chat is blocked for a specific reason.
     *
     * @property message Friendly user-facing reason.
     * @property isRecoverable Whether the user can fix this (e.g., by selecting a different model).
     */
    data class Blocked(
        val message: String,
        val isRecoverable: Boolean = true
    ) : ChatReadiness

    /**
     * Chat is waiting for a background process (e.g., model loading).
     *
     * @property message Progress message.
     */
    data class Loading(val message: String) : ChatReadiness
}
