package com.localaichat.domain.repository

import com.localaichat.domain.model.ChatMessage
import com.localaichat.domain.model.ChatRole
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(conversationId: Long = DEFAULT_CONVERSATION_ID): Flow<List<ChatMessage>>
    suspend fun getMessages(conversationId: Long = DEFAULT_CONVERSATION_ID): List<ChatMessage>
    suspend fun insertMessage(
        role: ChatRole,
        content: String,
        conversationId: Long = DEFAULT_CONVERSATION_ID,
    ): Long

    suspend fun updateMessage(
        messageId: Long,
        content: String,
    )

    suspend fun deleteMessage(messageId: Long)

    suspend fun clearConversation(conversationId: Long = DEFAULT_CONVERSATION_ID)

    companion object {
        const val DEFAULT_CONVERSATION_ID = 1L
    }
}
