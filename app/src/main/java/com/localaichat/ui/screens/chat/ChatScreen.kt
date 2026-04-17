package com.localaichat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localaichat.ui.components.MessageBubble

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("LocalAIChat")
                        Text(
                            text = "Model: ${uiState.selectedModelName.ifBlank { "None selected" }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Active: ${uiState.activeModelName.ifBlank { "No active model" }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onClearConversation) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = "Clear chat",
                        )
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
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("${uiState.maxTokens} max tokens") },
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Temp ${"%.2f".format(uiState.temperature)}") },
                )
                AssistChip(
                    onClick = {},
                    label = { Text(uiState.selectedModelStatus.asLabel()) },
                )
                if (uiState.activeModelName.isNotBlank()) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Active ${uiState.activeModelName}") },
                    )
                }
            }

            uiState.conversationState.messageOrNull()?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (uiState.conversationState) {
                        is ChatConversationState.Failed,
                        is ChatConversationState.NoModelSelected,
                        is ChatConversationState.ModelNotReady,
                        is ChatConversationState.Cancelled -> MaterialTheme.colorScheme.error

                        is ChatConversationState.Generating,
                        ChatConversationState.Idle -> MaterialTheme.colorScheme.primary
                    },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (uiState.messages.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Start a conversation",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (uiState.canSend || uiState.conversationState == ChatConversationState.Idle) {
                                "This scaffold persists messages locally and streams assistant output into the timeline."
                            } else {
                                uiState.conversationState.messageOrNull().orEmpty()
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.canSend || uiState.conversationState == ChatConversationState.Idle) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(message = message)
                        }
                        item {
                            Box(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = uiState.draftPrompt,
                onValueChange = onPromptChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.conversationState !is ChatConversationState.Generating,
                placeholder = {
                    Text(
                        if (uiState.canSend) {
                            "Ask the local model something..."
                        } else {
                            "Select and load a local model first"
                        },
                    )
                },
                minLines = 3,
                maxLines = 6,
                supportingText = {
                    val supportingMessage = when (val state = uiState.conversationState) {
                        is ChatConversationState.NoModelSelected -> state.message
                        is ChatConversationState.ModelNotReady -> state.message
                        is ChatConversationState.Cancelled -> state.message
                        is ChatConversationState.Failed -> state.message
                        is ChatConversationState.Generating -> "Streaming with ${state.modelName}."
                        ChatConversationState.Idle -> null
                    }
                    if (!supportingMessage.isNullOrBlank()) {
                        Text(supportingMessage)
                    }
                },
                trailingIcon = {
                    if (uiState.conversationState is ChatConversationState.Generating) {
                        IconButton(onClick = onStopGeneration) {
                            Icon(
                                imageVector = Icons.Outlined.StopCircle,
                                contentDescription = "Stop generation",
                            )
                        }
                    } else {
                        FilledIconButton(
                            onClick = onSendMessage,
                            enabled = uiState.canSend && uiState.draftPrompt.isNotBlank(),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = "Send message",
                            )
                        }
                    }
                },
            )
        }
    }
}

private fun com.localaichat.domain.model.ModelStatus.asLabel(): String = when (this) {
    com.localaichat.domain.model.ModelStatus.NotLoaded -> "Not loaded"
    com.localaichat.domain.model.ModelStatus.Loading -> "Loading"
    com.localaichat.domain.model.ModelStatus.Initializing -> "Initializing"
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
