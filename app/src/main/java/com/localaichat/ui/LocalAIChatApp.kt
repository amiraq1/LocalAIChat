package com.localaichat.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localaichat.LocalAIChatApplication
import com.localaichat.ui.navigation.AppRoute
import com.localaichat.ui.navigation.TopLevelDestination
import com.localaichat.ui.screens.chat.ChatScreen
import com.localaichat.ui.screens.chat.ChatViewModel
import com.localaichat.ui.screens.model.ModelScreen
import com.localaichat.ui.screens.model.ModelViewModel
import com.localaichat.ui.screens.settings.SettingsScreen
import com.localaichat.ui.screens.settings.SettingsViewModel

@Composable
fun LocalAIChatApp() {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { navDestination ->
                            when (destination) {
                                TopLevelDestination.Chat -> navDestination.hasRoute(AppRoute.Chat::class)
                                TopLevelDestination.Models -> navDestination.hasRoute(AppRoute.Models::class)
                                TopLevelDestination.Settings -> navDestination.hasRoute(AppRoute.Settings::class)
                            }
                        } == true,
                        onClick = {
                            navController.navigate(destination.toRoute()) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    TopLevelDestination.Chat -> Icons.Outlined.ChatBubbleOutline
                                    TopLevelDestination.Models -> Icons.Outlined.Memory
                                    TopLevelDestination.Settings -> Icons.Outlined.Settings
                                },
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Chat,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            composable<AppRoute.Chat> {
                val viewModel: ChatViewModel = viewModel(factory = appViewModelFactory())
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ChatScreen(
                    uiState = uiState,
                    onPromptChange = viewModel::onPromptChange,
                    onSendMessage = viewModel::sendMessage,
                    onStopGeneration = viewModel::stopGeneration,
                    onClearConversation = viewModel::clearConversation,
                )
            }

            composable<AppRoute.Models> {
                val viewModel: ModelViewModel = viewModel(factory = appViewModelFactory())
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ModelScreen(
                    uiState = uiState,
                    onModelSelected = viewModel::selectModel,
                    onInstallModel = viewModel::installModel,
                    onCancelInstall = viewModel::cancelInstall,
                    onLoadModel = viewModel::loadModel,
                    onCancelLoad = viewModel::cancelLoad,
                    onUnloadModel = viewModel::unloadModel,
                )
            }

            composable<AppRoute.Settings> {
                val viewModel: SettingsViewModel = viewModel(factory = appViewModelFactory())
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SettingsScreen(
                    uiState = uiState,
                    onMaxTokensChanged = viewModel::updateMaxTokens,
                    onTemperatureChanged = viewModel::updateTemperature,
                    onBackendSelected = viewModel::selectBackend,
                )
            }
        }
    }
}

private fun TopLevelDestination.toRoute(): AppRoute = when (this) {
    TopLevelDestination.Chat -> AppRoute.Chat
    TopLevelDestination.Models -> AppRoute.Models
    TopLevelDestination.Settings -> AppRoute.Settings
}

private fun appViewModelFactory(): Factory = viewModelFactory {
    initializer {
        val application = this[APPLICATION_KEY] as LocalAIChatApplication
        ChatViewModel(application, application.container)
    }
    initializer {
        val application = this[APPLICATION_KEY] as LocalAIChatApplication
        SettingsViewModel(application, application.container)
    }
    initializer {
        val application = this[APPLICATION_KEY] as LocalAIChatApplication
        ModelViewModel(application, application.container)
    }
}
