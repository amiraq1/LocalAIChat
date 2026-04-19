package com.localaichat.data.repository

import com.localaichat.domain.model.BackendStatus
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.InferenceAdapter
import com.localaichat.domain.repository.LocalModelLoadingWorkflow
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class BackendAwareLocalModelLoadingWorkflow(
    private val settingsRepository: SettingsRepository,
    private val backendManager: BackendManager,
    private val mediaPipeAdapter: InferenceAdapter,
) : LocalModelLoadingWorkflow {

    override fun load(model: ModelOption): Flow<LocalModelLoadEvent> = flow {
        val backendType = settingsRepository.getBackendType()
        val backendOption = backendManager.observeAvailableBackends().first()
            .firstOrNull { it.type == backendType }
        val backendStatus = backendOption?.status ?: BackendStatus.Unavailable("Selected backend not found.")

        if (backendStatus is BackendStatus.Unavailable) {
            emit(
                LocalModelLoadEvent.Failed(
                    "${backendOption?.displayName ?: backendType.name} is unavailable: ${backendStatus.reason}"
                )
            )
            return@flow
        }

        when (backendType) {
            BackendType.MEDIAPIPE -> emitAll(
                mediaPipeAdapter.initializeModel(
                    InferenceModelHandle(
                        id = model.id,
                        localPath = model.localPath,
                        format = model.localPath.substringAfterLast('.', ""),
                    ),
                )
            )

            BackendType.FAKE,
            BackendType.LLAMA_CPP -> emit(LocalModelLoadEvent.Ready)

            BackendType.ONNX_RUNTIME -> emit(
                LocalModelLoadEvent.Failed("ONNX Runtime is not wired yet.")
            )
        }
    }
}
