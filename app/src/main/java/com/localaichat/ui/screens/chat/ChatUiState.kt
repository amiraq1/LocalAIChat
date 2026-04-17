package com.localaichat.ui.screens.chat

import com.localaichat.domain.model.ChatMessage
import com.localaichat.domain.model.ChatReadiness
import com.localaichat.domain.model.ModelStatus

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val draftPrompt: String = "",
    val selectedModelName: String = "",
    val activeModelName: String = "",
    val selectedModelStatus: ModelStatus = ModelStatus.NotLoaded,
    val readiness: ChatReadiness = ChatReadiness.Blocked("Initializing..."),
    val conversationState: ChatConversationState = ChatConversationState.NoModelSelected(
        "Select a local model to start chatting.",
    ),
    val canSend: Boolean = false,
    val maxTokens: Int = 512,
    val temperature: Float = 0.7f,
)
