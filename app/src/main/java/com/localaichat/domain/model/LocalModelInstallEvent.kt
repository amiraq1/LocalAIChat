package com.localaichat.domain.model

sealed interface LocalModelInstallEvent {
    data class Downloading(val progressPercent: Int) : LocalModelInstallEvent
    data class Installing(val progressPercent: Int) : LocalModelInstallEvent
    data object Installed : LocalModelInstallEvent
    data class Failed(val userMessage: String) : LocalModelInstallEvent
}
