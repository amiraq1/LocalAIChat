package com.localaichat.domain.model

import kotlinx.serialization.Serializable

/**
 * Supported inference backends.
 */
@Serializable
enum class BackendType {
    FAKE,
    MEDIAPIPE,
    ONNX_RUNTIME,
    LLAMA_CPP,
}
