package com.localaichat.domain.repository

import com.localaichat.domain.model.LocalModelInstallEvent
import com.localaichat.domain.model.ModelOption
import kotlinx.coroutines.flow.Flow

interface LocalModelInstallationWorkflow {
    fun install(model: ModelOption): Flow<LocalModelInstallEvent>
}
