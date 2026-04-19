package com.localaichat.data.repository

import com.localaichat.domain.model.BackendStatus
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.LlmEngine
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * An [LlmEngine] implementation that delegates to a specific backend based on
 * the current app settings.
 */
class DynamicLlmEngine(
    private val settingsRepository: SettingsRepository,
    private val backendManager: BackendManager,
    private val fakeEngine: LlmEngine,
    private val mediaPipeEngine: LlmEngine,
    private val llamaCppEngine: LlmEngine,
) : LlmEngine {

    override fun streamReply(
        renderedPrompt: RenderedPrompt,
        config: GenerationConfig,
        model: ModelOption,
    ): Flow<String> {
        return combine(
            settingsRepository.observeBackendType(),
            backendManager.observeAvailableBackends(),
        ) { selectedType, availableBackends ->
            val option = availableBackends.find { it.type == selectedType }
            val status = option?.status ?: BackendStatus.Unavailable("Unknown backend type.")
            selectedType to status
        }.flatMapLatest { (type, status) ->
            when (status) {
                is BackendStatus.Available -> {
                    val engine = when (type) {
                        BackendType.LLAMA_CPP -> llamaCppEngine
                        BackendType.FAKE -> fakeEngine
                        BackendType.MEDIAPIPE -> mediaPipeEngine
                        else -> fakeEngine
                    }
                    engine.streamReply(renderedPrompt, config, model)
                }

                is BackendStatus.Unavailable -> flow {
                    emit("[Backend Fallback: ${type.name} is unavailable. Reason: ${status.reason}]\n\n")
                    fakeEngine.streamReply(renderedPrompt, config, model).collect {
                        emit(it)
                    }
                }
            }
        }
    }
}
