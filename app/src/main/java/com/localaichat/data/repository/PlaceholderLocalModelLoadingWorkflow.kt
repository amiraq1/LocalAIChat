package com.localaichat.data.repository

import com.localaichat.domain.model.LocalModelLoadEvent
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.repository.LocalModelLoadingWorkflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceholderLocalModelLoadingWorkflow : LocalModelLoadingWorkflow {
    override fun load(model: ModelOption): Flow<LocalModelLoadEvent> = flow {
        listOf(15, 40, 70, 100).forEach { progress ->
            delay(130)
            emit(LocalModelLoadEvent.Loading(progress))
        }
        
        listOf(10, 50, 90).forEach { progress ->
            delay(100)
            emit(LocalModelLoadEvent.Initializing(progress))
        }
        
        emit(LocalModelLoadEvent.Ready)
    }
}
