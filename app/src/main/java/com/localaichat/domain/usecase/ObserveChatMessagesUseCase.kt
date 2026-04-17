package com.localaichat.domain.usecase

import com.localaichat.domain.repository.ChatRepository

class ObserveChatMessagesUseCase(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke() = chatRepository.observeMessages()
}
