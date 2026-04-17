package com.localaichat.domain.usecase

import com.localaichat.data.repository.GemmaPromptFormatter
import com.localaichat.data.repository.PlainPromptFormatter
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationEvent
import com.localaichat.domain.model.ChatRole
import com.localaichat.domain.model.PromptInput
import com.localaichat.domain.repository.ChatRepository
import com.localaichat.domain.repository.LlmEngine
import com.localaichat.domain.repository.ModelManager
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SendMessageUseCase(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val modelManager: ModelManager,
    private val llmEngine: LlmEngine,
) {
    operator fun invoke(prompt: String): Flow<GenerationEvent> = flow {
        val managerState = modelManager.observeManagerState().first()
        val selectedModel = modelManager.getSelectedModelOrNull()
        if (selectedModel == null || !managerState.isSelectedModelReady) {
            emit(GenerationEvent.Rejected(managerState.selectedModelAvailabilityMessage))
            return@flow
        }

        chatRepository.insertMessage(role = ChatRole.USER, content = prompt)
        val history = chatRepository.getMessages()
        val assistantMessageId = chatRepository.insertMessage(
            role = ChatRole.ASSISTANT,
            content = "",
        )

        var accumulated = ""
        var selectedModelId: String? = null

        try {
            selectedModelId = selectedModel.id
            val generationConfig = settingsRepository.getGenerationConfig()
            val backendType = settingsRepository.getBackendType()

            // Choose a formatter based on the backend. 
            // Real apps would map this to model architectures.
            val formatter = when (backendType) {
                BackendType.MEDIAPIPE -> GemmaPromptFormatter()
                else -> PlainPromptFormatter()
            }

            val renderedPrompt = formatter.format(
                PromptInput(
                    history = history,
                    systemPrompt = "You are a helpful local AI assistant."
                )
            )

            emit(GenerationEvent.Started(selectedModel))
            llmEngine.streamReply(
                renderedPrompt = renderedPrompt,
                config = generationConfig,
                model = selectedModel,
            ).collect { chunk ->
                accumulated += chunk
                chatRepository.updateMessage(
                    messageId = assistantMessageId,
                    content = accumulated,
                )
                emit(GenerationEvent.Delta(chunk, accumulated))
            }
            emit(GenerationEvent.Completed(assistantMessageId, accumulated))
        } catch (cancellationException: CancellationException) {
            withContext(NonCancellable) {
                if (accumulated.isBlank()) {
                    chatRepository.deleteMessage(assistantMessageId)
                } else {
                    chatRepository.updateMessage(assistantMessageId, accumulated)
                }
            }
            throw cancellationException
        } catch (throwable: Throwable) {
            val reason = throwable.message ?: "Unknown local inference error."
            selectedModelId?.let { modelManager.markModelFailed(it, reason) }
            withContext(NonCancellable) {
                val failureText = if (accumulated.isBlank()) {
                    "Generation failed: $reason"
                } else {
                    "$accumulated\n\n[Generation failed: $reason]"
                }
                chatRepository.updateMessage(assistantMessageId, failureText)
            }
            emit(GenerationEvent.Failed(assistantMessageId, reason))
        }
    }
}
