package com.localaichat.data.backend.real

import com.localaichat.domain.model.InferenceModelHandle
import com.localaichat.domain.model.LocalModelLoadEvent
import kotlinx.coroutines.flow.Flow

/**
 * Validates model files before a backend runtime attempts initialization.
 */
interface RealBackendModelFileValidator {
    fun validate(model: InferenceModelHandle): Flow<LocalModelLoadEvent>
}
