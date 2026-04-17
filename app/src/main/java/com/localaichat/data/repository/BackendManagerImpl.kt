package com.localaichat.data.repository

import com.localaichat.domain.model.BackendOption
import com.localaichat.domain.model.BackendStatus
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BackendManagerImpl(
    private val settingsRepository: SettingsRepository,
) : BackendManager {

    override fun observeAvailableBackends(): Flow<List<BackendOption>> =
        settingsRepository.observeBackendType().map { _ ->
            listOf(
                BackendOption(
                    type = BackendType.FAKE,
                    displayName = "Fake Backend",
                    description = "Fast, non-functional placeholder for development and testing.",
                    status = BackendStatus.Available,
                ),
                BackendOption(
                    type = BackendType.MEDIAPIPE,
                    displayName = "MediaPipe LLM",
                    description = "On-device inference using MediaPipe LLM Inference task.",
                    // TODO: Change status to Available once dependencies are added.
                    status = BackendStatus.Unavailable(
                        reason = "MediaPipe is not yet integrated into the runtime.",
                        isFixable = false,
                    ),
                ),
                BackendOption(
                    type = BackendType.ONNX_RUNTIME,
                    displayName = "ONNX Runtime",
                    description = "Cross-platform inference for ONNX models.",
                    status = BackendStatus.Unavailable(
                        reason = "ONNX Runtime is not yet integrated.",
                        isFixable = false,
                    ),
                ),
                BackendOption(
                    type = BackendType.LLAMA_CPP,
                    displayName = "llama.cpp",
                    description = "Native GGUF inference via llama.cpp JNI.",
                    status = BackendStatus.Unavailable(
                        reason = "llama.cpp is not yet integrated.",
                        isFixable = false,
                    ),
                )
            )
        }

    override fun observeSelectedBackendType(): Flow<BackendType> =
        settingsRepository.observeBackendType()

    override suspend fun selectBackend(type: BackendType) {
        settingsRepository.updateBackendType(type)
    }
}
