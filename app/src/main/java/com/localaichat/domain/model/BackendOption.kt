package com.localaichat.domain.model

/**
 * Representation of an inference backend option for the UI.
 */
data class BackendOption(
    val type: BackendType,
    val displayName: String,
    val description: String,
    val status: BackendStatus,
) {
    val isAvailable: Boolean get() = status is BackendStatus.Available
}
