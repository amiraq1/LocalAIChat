package com.localaichat.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localaichat.ui.components.MessageBubble
import com.localaichat.ui.theme.DeepCharcoal
import com.localaichat.ui.theme.ErrorRed
import com.localaichat.ui.theme.GhostWhite
import com.localaichat.ui.theme.MidCharcoal
import com.localaichat.ui.theme.NeonCyan
import com.localaichat.ui.theme.NeonCyanDim
import com.localaichat.ui.theme.PureWhite
import com.localaichat.ui.theme.SoftWhite
import com.localaichat.ui.theme.SubtleGray
import com.localaichat.ui.theme.SuccessGreen
import com.localaichat.ui.theme.VoidBlack
import com.localaichat.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onPromptChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStopGeneration: () -> Unit,
    onClearConversation: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.messages.lastOrNull()?.content) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    val isGenerating = uiState.conversationState is ChatConversationState.Generating

    Scaffold(
        containerColor = VoidBlack,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepCharcoal.copy(alpha = 0.95f),
                    titleContentColor = PureWhite,
                    actionIconContentColor = GhostWhite,
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pulsating status dot
                        val dotColor by animateColorAsState(
                            targetValue = when {
                                isGenerating -> WarningOrange
                                uiState.canSend -> SuccessGreen
                                else -> ErrorRed
                            },
                            animationSpec = tween(600),
                            label = "statusDot"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LocalAI Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                            )
                            Text(
                                text = uiState.activeModelName.ifBlank {
                                    uiState.selectedModelName.ifBlank { "No model" }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (uiState.canSend) NeonCyan.copy(alpha = 0.8f) else GhostWhite,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.messages.isNotEmpty()) {
                        IconButton(onClick = onClearConversation) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Clear chat",
                                tint = GhostWhite,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .background(VoidBlack),
        ) {
            // ── Status bar ───────────────────────────────
            AnimatedVisibility(
                visible = uiState.conversationState !is ChatConversationState.Idle,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut(),
            ) {
                uiState.conversationState.messageOrNull()?.let { message ->
                    val statusColor = when (uiState.conversationState) {
                        is ChatConversationState.Failed,
                        is ChatConversationState.NoModelSelected,
                        is ChatConversationState.ModelNotReady,
                        is ChatConversationState.Cancelled -> ErrorRed

                        is ChatConversationState.Generating -> NeonCyan
                        ChatConversationState.Idle -> NeonCyan
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(statusColor.copy(alpha = 0.08f))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            letterSpacing = 0.3.sp,
                        )
                    }
                }
            }

            // ── Message list ─────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (uiState.messages.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            NeonCyan.copy(alpha = 0.15f),
                                            Color.Transparent,
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Psychology,
                                contentDescription = null,
                                tint = NeonCyan.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Text(
                            text = "Start a conversation",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                        )
                        Text(
                            text = if (uiState.canSend) {
                                "Type a message below to chat with your local AI model."
                            } else {
                                uiState.conversationState.messageOrNull()
                                    ?: "Select and load a model from the Models tab."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.canSend) GhostWhite else ErrorRed.copy(alpha = 0.8f),
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 12.dp, bottom = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(message = message)
                        }
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // ── Input area ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepCharcoal.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextField(
                        value = uiState.draftPrompt,
                        onValueChange = onPromptChange,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .border(
                                width = 0.5.dp,
                                color = if (isGenerating) WarningOrange.copy(alpha = 0.3f)
                                else SubtleGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        enabled = !isGenerating,
                        placeholder = {
                            Text(
                                text = if (uiState.canSend) "Message..." else "Load a model first",
                                color = GhostWhite,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidCharcoal,
                            unfocusedContainerColor = MidCharcoal,
                            disabledContainerColor = MidCharcoal.copy(alpha = 0.5f),
                            cursorColor = NeonCyan,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = SoftWhite,
                        ),
                        maxLines = 5,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )

                    // Send / Stop button
                    val buttonEnabled = if (isGenerating) true
                    else uiState.canSend && uiState.draftPrompt.isNotBlank()

                    val buttonBg by animateColorAsState(
                        targetValue = when {
                            isGenerating -> ErrorRed
                            buttonEnabled -> NeonCyan
                            else -> SubtleGray
                        },
                        animationSpec = tween(300),
                        label = "btnColor"
                    )

                    IconButton(
                        onClick = {
                            if (isGenerating) onStopGeneration() else onSendMessage()
                        },
                        enabled = buttonEnabled,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (buttonEnabled) buttonBg else SubtleGray),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = VoidBlack,
                            disabledContentColor = GhostWhite,
                        ),
                    ) {
                        Icon(
                            imageVector = if (isGenerating) Icons.Outlined.StopCircle
                            else Icons.AutoMirrored.Outlined.Send,
                            contentDescription = if (isGenerating) "Stop" else "Send",
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun com.localaichat.domain.model.ModelStatus.asLabel(): String = when (this) {
    com.localaichat.domain.model.ModelStatus.NotLoaded -> "Not loaded"
    is com.localaichat.domain.model.ModelStatus.Loading -> "Loading"
    is com.localaichat.domain.model.ModelStatus.Initializing -> "Initializing"
    com.localaichat.domain.model.ModelStatus.Ready -> "Ready"
    is com.localaichat.domain.model.ModelStatus.Failed -> "Failed"
}

private fun ChatConversationState.messageOrNull(): String? = when (this) {
    ChatConversationState.Idle -> null
    is ChatConversationState.NoModelSelected -> this.message
    is ChatConversationState.ModelNotReady -> this.message
    is ChatConversationState.Generating -> "Generating with ${this.modelName}..."
    is ChatConversationState.Cancelled -> this.message
    is ChatConversationState.Failed -> this.message
}
