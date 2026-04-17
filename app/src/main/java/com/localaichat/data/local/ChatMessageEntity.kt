package com.localaichat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.localaichat.domain.model.ChatMessage
import com.localaichat.domain.model.ChatRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: String,
    val content: String,
    val timestamp: Long,
)

fun ChatMessageEntity.asDomain(): ChatMessage = ChatMessage(
    id = id,
    conversationId = conversationId,
    role = ChatRole.valueOf(role),
    content = content,
    timestamp = timestamp,
)
