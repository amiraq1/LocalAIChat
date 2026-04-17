package com.localaichat.data.backend.mediapipe

import com.localaichat.data.backend.real.RealBackendModelFileValidator
import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.RealModelHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * MediaPipe-specific model file validator.
 */
class MediaPipeModelValidator : RealBackendModelFileValidator {
    override fun validate(model: RealModelHandle): Flow<LocalModelLoadEvent> = flow {
        val file = File(model.localPath)
        if (!file.exists()) {
            emit(LocalModelLoadEvent.Failed("Model file does not exist at ${model.localPath}"))
            return@flow
        }
        if (!model.localPath.endsWith(".bin")) {
            emit(LocalModelLoadEvent.Failed("MediaPipe models must be in .bin format."))
            return@flow
        }
        emit(LocalModelLoadEvent.Loading(100))
    }
}
