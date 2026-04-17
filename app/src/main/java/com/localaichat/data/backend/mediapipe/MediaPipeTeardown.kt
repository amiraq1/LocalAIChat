package com.localaichat.data.backend.mediapipe

import com.localaichat.data.backend.real.RealBackendTeardown

/**
 * MediaPipe-specific teardown handler.
 */
class MediaPipeTeardown : RealBackendTeardown {
    override suspend fun teardown() {
        // TODO: Call llmInference.close() and clear the instance reference.
        // This ensures native memory and GPU delegates are released.
    }
}
