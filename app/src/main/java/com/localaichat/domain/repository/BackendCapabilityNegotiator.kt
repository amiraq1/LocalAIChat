package com.localaichat.domain.repository

import com.localaichat.domain.model.BackendCapabilities

/**
 * Optional boundary for backends that can describe their capabilities before the
 * app starts model initialization or prompt submission.
 *
 * This stays separate from [LlmEngine] so the default fake implementation can
 * continue working unchanged.
 */
interface BackendCapabilityNegotiator {
    suspend fun getBackendCapabilities(): BackendCapabilities
}
