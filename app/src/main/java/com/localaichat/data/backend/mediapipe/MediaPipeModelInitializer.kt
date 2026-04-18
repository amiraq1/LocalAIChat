package com.localaichat.data.backend.mediapipe

import com.localaichat.data.backend.real.AndroidBackendContext
import com.localaichat.data.backend.real.RealBackendModelInitializer
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * MediaPipe-specific runtime initializer.
 */
class MediaPipeModelInitializer(
    private val androidContext: AndroidBackendContext
) : RealBackendModelInitializer {
    override fun initialize(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        emit(LocalModelLoadEvent.Initializing(10))

        // TODO: Map handle to LlmInferenceOptions
        // val options = LlmInference.LlmInferenceOptions.builder()
        //     .setModelPath(model.localPath)
        //     .setMaxTokens(2048)
        //     .setResultListener { result, done -> /* emit results */ }
        //     .build()

        // TODO: Instantiate the actual native engine
        // llmInference = LlmInference.createFromOptions(androidContext.applicationContext, options)

        emit(LocalModelLoadEvent.Initializing(100))
        emit(LocalModelLoadEvent.Ready)
    }
}
