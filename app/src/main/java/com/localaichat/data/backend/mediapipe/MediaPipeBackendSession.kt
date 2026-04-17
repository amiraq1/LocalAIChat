package com.localaichat.data.backend.mediapipe

import com.localaichat.data.backend.real.AndroidBackendContext
import com.localaichat.data.backend.real.RealBackendModelFileValidator
import com.localaichat.data.backend.real.RealBackendModelInitializer
import com.localaichat.data.backend.real.RealBackendPromptExecutor
import com.localaichat.data.backend.real.RealBackendSession
import com.localaichat.data.backend.real.RealBackendTeardown
import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * MediaPipe-specific session implementation.
 */
class MediaPipeBackendSession(
    private val model: InferenceModelHandle,
    private val androidContext: AndroidBackendContext,
    private val modelValidator: RealBackendModelFileValidator = MediaPipeModelValidator(),
    private val modelInitializer: RealBackendModelInitializer = MediaPipeModelInitializer(androidContext),
    private val promptExecutor: RealBackendPromptExecutor = MediaPipePromptExecutor(),
    private val teardownHandler: RealBackendTeardown = MediaPipeTeardown(),
) : RealBackendSession {

    // TODO: private var llmInference: LlmInference? = null
    // This session owns the lifecycle of the native MediaPipe object.

    override fun initializeModel(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        emitAll(modelValidator.validate(model))
        emitAll(modelInitializer.initialize(model))
    }

    override fun executeInference(request: InferenceRequest): Flow<InferenceEvent> =
        promptExecutor.execute(request)

    override suspend fun cancel(requestId: String) {
        promptExecutor.cancel(requestId)
    }

    override suspend fun teardown() {
        teardownHandler.teardown()
    }
}
