package com.localaichat.data.backend.real

import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow

/**
 * Initializes backend-specific runtime resources for a validated model.
 */
interface RealBackendModelInitializer {
    fun initialize(model: InferenceModelHandle): Flow<LocalModelLoadEvent>
}
