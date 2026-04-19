package com.localaichat.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.localaichat.ui.theme.DeepCharcoal
import com.localaichat.ui.theme.GhostWhite
import com.localaichat.ui.theme.NeonCyan
import com.localaichat.ui.theme.SubtleGray
import com.localaichat.ui.theme.VoidBlack

@Composable
fun LocalAIChatApp() {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries

    Scaffold(
        containerColor = VoidBlack,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepCharcoal.copy(alpha = 0.97f))
                    .border(
                        width = 0.5.dp,
                        color = SubtleGray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    destinations.forEach { destination ->
                        val isSelected = currentDestination?.hierarchy?.any { navDestination ->
                            when (destination) {
                                TopLevelDestination.Chat -> navDestination.hasRoute(AppRoute.Chat::class)
                                TopLevelDestination.Models -> navDestination.hasRoute(AppRoute.Models::class)
                                TopLevelDestination.Settings -> navDestination.hasRoute(AppRoute.Settings::class)
                            }
                        } == true

                        val iconTint by animateColorAsState(
                            targetValue = if (isSelected) NeonCyan else GhostWhite,
                            animationSpec = tween(250),
                            label = "navIconTint"
                        )

                        NavigationBarItem(
                            selected = isSelected,
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    // Accent indicator line
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(2.dp)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(NeonCyan)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                    Icon(
                                        imageVector = when (destination) {
                                            TopLevelDestination.Chat -> Icons.Rounded.ChatBubbleOutline
                                            TopLevelDestination.Models -> Icons.Rounded.Memory
                                            TopLevelDestination.Settings -> Icons.Rounded.Settings
                                        },
                                        contentDescription = destination.label,
                                        tint = iconTint,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else GhostWhite,
                                    letterSpacing = 0.3.sp,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Chat,
            modifier = Modifier
                .fillMaxSize()
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
                    onServerUrlChanged = viewModel::updateServerUrl,
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
