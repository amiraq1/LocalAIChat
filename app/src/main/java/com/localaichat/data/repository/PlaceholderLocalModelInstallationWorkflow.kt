package com.localaichat.data.repository

import com.localaichat.domain.model.LocalModelInstallEvent
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.repository.LocalModelInstallationWorkflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceholderLocalModelInstallationWorkflow : LocalModelInstallationWorkflow {
    override fun install(model: ModelOption): Flow<LocalModelInstallEvent> = flow {
        if (model.localPath.isNullOrBlank()) {
            emit(LocalModelInstallEvent.Failed("No local install path is configured for ${model.name}."))
            return@flow
        }

        listOf(10, 35, 60, 85, 100).forEach { progress ->
            delay(120)
            emit(LocalModelInstallEvent.Downloading(progress))
        }
        listOf(20, 55, 100).forEach { progress ->
            delay(140)
            emit(LocalModelInstallEvent.Installing(progress))
        }
        emit(LocalModelInstallEvent.Installed)
    }
}
