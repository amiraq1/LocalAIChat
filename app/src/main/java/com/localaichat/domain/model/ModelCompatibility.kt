package com.localaichat.domain.model

/**
 * Result of checking if a model is compatible with a specific backend.
 */
sealed interface ModelCompatibility {
    /**
     * Model is compatible with the selected backend.
     */
    object Compatible : ModelCompatibility

    /**
     * Model is NOT compatible with the selected backend.
     *
     * @property reason Human-readable explanation of why the model cannot be used.
     */
    data class Incompatible(val reason: String) : ModelCompatibility
}
