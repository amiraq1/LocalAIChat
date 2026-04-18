package com.localaichat.data.backend.mediapipe

import com.localaichat.domain.model.BackendCapabilities
import com.localaichat.domain.model.InferenceEvent
import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.InferenceRequest
import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.ModelFormat
import com.localaichat.domain.repository.InferenceAdapter
import com.localaichat.data.backend.real.AndroidBackendContext
import com.localaichat.data.backend.real.RealBackendSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * MediaPipe-based implementation of [InferenceAdapter].
 */
class MediaPipeInferenceAdapter(
    private val androidContext: AndroidBackendContext,
    private val sessionFactory: (InferenceModelHandle, AndroidBackendContext) -> RealBackendSession = { model, context ->
        MediaPipeBackendSession(model, androidContext = context)
    }
) : InferenceAdapter {

    private val sessionMutex = Mutex()
    private var activeSession: RealBackendSession? = null
    private var activeModelId: String? = null

    override suspend fun getBackendCapabilities(): BackendCapabilities {
        return BackendCapabilities(
            backendId = "mediapipe",
            displayName = "MediaPipe LLM",
            supportsStreaming = true,
            supportsCancellation = true,
            maxContextSize = 2048,
            supportedModelFormats = setOf(ModelFormat.MediapipeBundle),
        )
    }

    override fun initializeModel(model: InferenceModelHandle): Flow<LocalModelLoadEvent> = flow {
        val session = sessionMutex.withLock {
            if (activeModelId == model.id && activeSession != null) {
                emit(LocalModelLoadEvent.Ready)
                return@flow
            }
            
            activeSession?.teardown()
            activeSession = null
            activeModelId = null
            
            val newSession = sessionFactory(model, androidContext)
            activeSession = newSession
            activeModelId = model.id
            newSession
        }

        session.initializeModel(model)
            .onEach { event -> emit(event) }
            .onCompletion { error ->
                if (error != null) {
                    sessionMutex.withLock {
                        activeSession = null
                        activeModelId = null
                    }
                }
            }
            .collect {}
    }

    override suspend fun teardownModel(modelId: String) = sessionMutex.withLock {
        if (activeModelId == modelId) {
            activeSession?.teardown()
            activeSession = null
            activeModelId = null
        }
    }

    override fun streamInference(request: InferenceRequest): Flow<InferenceEvent> {
        val session = activeSession
        if (session == null) {
            return flow {
                emit(
                    InferenceEvent.Failed(
                        requestId = request.requestId,
                        userMessage = "No active MediaPipe session. Initialize a model first.",
                    )
                )
            }
        }
        
        return session.executeInference(request)
    }

    override suspend fun cancelInference(requestId: String) {
        activeSession?.cancel(requestId)
    }
}
