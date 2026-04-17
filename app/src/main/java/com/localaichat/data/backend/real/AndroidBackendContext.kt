package com.localaichat.data.backend.real

import android.content.Context

/**
 * Provides access to the Android [Context] for backends that require it
 * (e.g., MediaPipe LLM Inference, TFLite).
 *
 * This remains in the data layer to prevent leaking Android framework types
 * into the domain layer.
 */
interface AndroidBackendContext {
    val applicationContext: Context
}

/**
 * Simple implementation of [AndroidBackendContext].
 */
class AndroidBackendContextProvider(
    override val applicationContext: Context
) : AndroidBackendContext
