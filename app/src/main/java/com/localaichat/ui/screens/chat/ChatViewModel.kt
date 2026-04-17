package com.localaichat.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localaichat.di.AppContainer
import com.localaichat.domain.model.ChatReadiness
import com.localaichat.domain.model.GenerationEvent
import com.localaichat.domain.model.ModelManagerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
    private val container: AppContainer,
) : AndroidViewModel(application) {
    private var generationJob: Job? = null
    private val draftPrompt = MutableStateFlow("")
    private val transientConversationState = MutableStateFlow<ChatConversationState>(ChatConversationState.Idle)
    private val modelManagerState = combine(
        container.modelManager.observeAvailableModels(),
        container.modelManager.observeManagerState(),
    ) { _, managerState ->
        managerState
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModelManagerState(),
    )

    val uiState = combine(
        container.observeChatMessagesUseCase(),
        draftPrompt,
        container.observeSettingsUseCase(),
        modelManagerState,
        transientConversationState,
        container.observeChatReadinessUseCase(),
    ) { messages, prompt, settings, managerState, transientState, readiness ->
        val effectiveState = effectiveConversationState(
            managerState = managerState,
            transientState = transientState,
        )
        ChatUiState(
            messages = messages,
            draftPrompt = prompt,
            selectedModelName = managerState.selectedModelName.orEmpty(),
            activeModelName = managerState.activeModelName.orEmpty(),
            selectedModelStatus = managerState.selectedModelStatus,
            readiness = readiness,
            conversationState = effectiveState,
            canSend = readiness is ChatReadiness.Ready &&
                effectiveState !is ChatConversationState.Generating,
            maxTokens = settings.generationConfig.maxTokens,
            temperature = settings.generationConfig.temperature,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState(),
    )

    fun onPromptChange(value: String) {
        draftPrompt.value = value
    }

    fun sendMessage() {
        val prompt = draftPrompt.value.trim()
        if (prompt.isBlank() || generationJob?.isActive == true) {
            return
        }

        val readiness = uiState.value.readiness
        if (readiness !is ChatReadiness.Ready) {
            // Log or handle blocked state if necessary, 
            // though UI should prevent this via canSend
            return
        }

        val managerState = modelManagerState.value
        draftPrompt.value = ""
        generationJob = viewModelScope.launch {
            transientConversationState.value = ChatConversationState.Generating(
                modelName = managerState.activeModelName
                    ?: managerState.selectedModelName
                    ?: "local model",
            )
            try {
                container.sendMessageUseCase(prompt).collect { event ->
                    when (event) {
                        is GenerationEvent.Started -> {
                            transientConversationState.value = ChatConversationState.Generating(event.model.name)
                        }

                        is GenerationEvent.Delta -> {
                            // Keep generating state updated
                        }

                        is GenerationEvent.Completed -> {
                            transientConversationState.value = ChatConversationState.Idle
                        }

                        is GenerationEvent.Failed -> {
                            transientConversationState.value = ChatConversationState.Failed(event.reason)
                        }

                        is GenerationEvent.Rejected -> {
                            transientConversationState.value = ChatConversationState.Failed(event.reason)
                        }
                    }
                }
            } catch (_: CancellationException) {
                transientConversationState.value = ChatConversationState.Cancelled("Generation stopped.")
            } finally {
                generationJob = null
            }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
    }

    fun clearConversation() {
        generationJob?.cancel()
        transientConversationState.value = defaultConversationState(modelManagerState.value)
        viewModelScope.launch {
            container.chatRepository.clearConversation()
        }
    }

    private fun effectiveConversationState(
        managerState: ModelManagerState,
        transientState: ChatConversationState,
    ): ChatConversationState = when (transientState) {
        ChatConversationState.Idle -> defaultConversationState(managerState)
        is ChatConversationState.NoModelSelected -> defaultConversationState(managerState)
        is ChatConversationState.ModelNotReady -> defaultConversationState(managerState)
        is ChatConversationState.Generating -> transientState
        is ChatConversationState.Cancelled -> transientState
        is ChatConversationState.Failed -> transientState
    }

    private fun defaultConversationState(managerState: ModelManagerState): ChatConversationState =
        unavailableStateFor(managerState) ?: ChatConversationState.Idle

    private fun unavailableStateFor(managerState: ModelManagerState): ChatConversationState? = when {
        !managerState.hasSelectedModel -> ChatConversationState.NoModelSelected(
            managerState.selectedModelAvailabilityMessage,
        )

        !managerState.isSelectedModelReady -> ChatConversationState.ModelNotReady(
            managerState.selectedModelAvailabilityMessage,
        )

        else -> null
    }
}
