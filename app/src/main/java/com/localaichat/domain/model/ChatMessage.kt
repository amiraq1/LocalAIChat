package com.localaichat.domain.model

data class ChatMessage(
    val id: Long,
    val conversationId: Long,
    val role: ChatRole,
    val content: String,
    val timestamp: Long,
)
