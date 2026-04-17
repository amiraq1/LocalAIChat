package com.localaichat.domain.model

sealed interface ModelStatus {
    data object NotLoaded : ModelStatus
    data class Loading(val progressPercent: Int) : ModelStatus
    data class Initializing(val progressPercent: Int) : ModelStatus
    data object Ready : ModelStatus
    data class Failed(val reason: String) : ModelStatus
}
