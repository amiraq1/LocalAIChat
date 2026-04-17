package com.localaichat.domain.model

/**
 * Availability status for a specific backend.
 */
sealed interface BackendStatus {
    /**
     * Backend is ready to be used.
     */
    object Available : BackendStatus

    /**
     * Backend is not available on this device or not yet integrated.
     *
     * @property reason Human-readable explanation.
     * @property isFixable Whether the user can do something (e.g., download a component).
     */
    data class Unavailable(
        val reason: String,
        val isFixable: Boolean = false,
    ) : BackendStatus
}
