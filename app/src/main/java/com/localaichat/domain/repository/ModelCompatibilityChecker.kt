package com.localaichat.domain.repository

import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.ModelCompatibility
import com.localaichat.domain.model.ModelOption

/**
 * Interface for checking model compatibility with backends.
 */
interface ModelCompatibilityChecker {
    /**
     * Check if a model is compatible with a specific backend type.
     */
    fun checkCompatibility(model: ModelOption, backendType: BackendType): ModelCompatibility
}
