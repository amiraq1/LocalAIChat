package com.localaichat.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Chat : AppRoute

    @Serializable
    data object Models : AppRoute

    @Serializable
    data object Settings : AppRoute
}

enum class TopLevelDestination(
    val label: String,
) {
    Chat(label = "Chat"),
    Models(label = "Models"),
    Settings(label = "Settings"),
}
