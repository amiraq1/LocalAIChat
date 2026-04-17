package com.localaichat.data.backend.real

/**
 * Releases all resources associated with an initialized real backend session.
 */
interface RealBackendTeardown {
    suspend fun teardown()
}
