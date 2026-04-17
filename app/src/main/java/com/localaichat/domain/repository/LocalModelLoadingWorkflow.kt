package com.localaichat.domain.repository

import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.ModelOption
import kotlinx.coroutines.flow.Flow

interface LocalModelLoadingWorkflow {
    fun load(model: ModelOption): Flow<LocalModelLoadEvent>
}
