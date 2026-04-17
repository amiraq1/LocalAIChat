package com.localaichat.data.repository

import com.localaichat.data.local.ChatDao
import com.localaichat.data.local.ChatMessageEntity
import com.localaichat.data.local.asDomain
import com.localaichat.domain.model.ChatMessage
import com.localaichat.domain.model.ChatRole
import com.localaichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
) : ChatRepository {
    override fun observeMessages(conversationId: Long): Flow<List<ChatMessage>> =
        chatDao.observeMessages(conversationId).map { entities ->
            entities.map { it.asDomain() }
        }

    override suspend fun getMessages(conversationId: Long): List<ChatMessage> =
        chatDao.getMessages(conversationId).map { it.asDomain() }

    override suspend fun insertMessage(
        role: ChatRole,
        content: String,
        conversationId: Long,
    ): Long = chatDao.insert(
        ChatMessageEntity(
            conversationId = conversationId,
            role = role.name,
            content = content,
            timestamp = System.currentTimeMillis(),
        ),
    )

    override suspend fun updateMessage(messageId: Long, content: String) {
        chatDao.updateMessageContent(messageId, content)
    }

    override suspend fun deleteMessage(messageId: Long) {
        chatDao.deleteMessage(messageId)
    }

    override suspend fun clearConversation(conversationId: Long) {
        chatDao.clearConversation(conversationId)
    }
}
