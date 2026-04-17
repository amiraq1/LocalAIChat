package com.localaichat.domain.repository

import com.localaichat.domain.model.LocalModelRegistryState
import kotlinx.coroutines.flow.Flow

interface LocalModelRegistry {
    fun observeRegistryState(): Flow<LocalModelRegistryState>
    suspend fun selectModel(modelId: String)
    suspend fun installModel(modelId: String)
    suspend fun cancelInstall(modelId: String)
    suspend fun loadModel(modelId: String)
    suspend fun cancelLoad(modelId: String)
    suspend fun unloadModel(modelId: String)
}
