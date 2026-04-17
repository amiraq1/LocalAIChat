package com.localaichat.ui.screens.chat

sealed interface ChatConversationState {
    data object Idle : ChatConversationState
    data class NoModelSelected(val message: String) : ChatConversationState
    data class ModelNotReady(val message: String) : ChatConversationState
    data class Generating(val modelName: String) : ChatConversationState
    data class Cancelled(val message: String) : ChatConversationState
    data class Failed(val message: String) : ChatConversationState
}
