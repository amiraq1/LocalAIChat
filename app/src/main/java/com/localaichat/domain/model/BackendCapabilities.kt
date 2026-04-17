package com.localaichat.domain.model

/**
 * Backend-agnostic capability description for any future inference runtime.
 *
 * These values let the app negotiate what a backend can do before binding model
 * lifecycle or generation flows to a specific implementation.
 */
data class BackendCapabilities(
    val backendId: String,
    val displayName: String,
    val supportsStreaming: Boolean,
    val supportsCancellation: Boolean,
    val maxContextSize: Int?,
    val supportedModelFormats: Set<ModelFormat>,
)

/**
 * Backend-agnostic model format identifiers.
 *
 * Keep these coarse grained so multiple Android runtimes can map into them
 * without leaking vendor-specific concepts into the app layer.
 */
sealed interface ModelFormat {
    data object Gguf : ModelFormat
    data object Safetensors : ModelFormat
    data object Onnx : ModelFormat
    data object Tflite : ModelFormat
    data object MediapipeBundle : ModelFormat
    data class Custom(val value: String) : ModelFormat
}
