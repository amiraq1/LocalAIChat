package com.localaichat.data.backend.real

import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Placeholder-only session implementation.
 */
class PlaceholderRealBackendSession(
    private val modelFileValidator: RealBackendModelFileValidator = PlaceholderRealBackendModelFileValidator(),
    private val modelInitializer: RealBackendModelInitializer = PlaceholderRealBackendModelInitializer(),
    private val promptExecutor: RealBackendPromptExecutor = PlaceholderRealBackendPromptExecutor(),
    private val teardownHandler: RealBackendTeardown = PlaceholderRealBackendTeardown(),
) : RealBackendSession {

    override fun initializeModel(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        emitAll(modelFileValidator.validate(model))
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

private class PlaceholderRealBackendModelFileValidator : RealBackendModelFileValidator {
    override fun validate(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        emit(LocalModelLoadEvent.Loading(100))
    }
}

private class PlaceholderRealBackendModelInitializer : RealBackendModelInitializer {
    override fun initialize(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        emit(LocalModelLoadEvent.Ready)
    }
}

private class PlaceholderRealBackendPromptExecutor : RealBackendPromptExecutor {
    override fun execute(request: InferenceRequest): Flow<InferenceEvent> = flow {
        emit(
            InferenceEvent.Failed(
                requestId = request.requestId,
                userMessage = "No real on-device backend is connected yet.",
            ),
        )
    }

    override suspend fun cancel(requestId: String) {
        // Intentionally blank.
    }
}

private class PlaceholderRealBackendTeardown : RealBackendTeardown {
    override suspend fun teardown() {
        // Intentionally blank.
    }
}
