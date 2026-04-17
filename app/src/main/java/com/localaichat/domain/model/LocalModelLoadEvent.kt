package com.localaichat.domain.model

sealed interface LocalModelLoadEvent {
    /**
     * Model is being loaded from disk into memory.
     */
    data class Loading(val progressPercent: Int) : LocalModelLoadEvent

    /**
     * Model is being initialized by the native backend runtime.
     */
    data class Initializing(val progressPercent: Int) : LocalModelLoadEvent

    /**
     * Model is ready for inference.
     */
    data object Ready : LocalModelLoadEvent

    /**
     * Model operation failed.
     */
    data class Failed(val userMessage: String) : LocalModelLoadEvent
}
