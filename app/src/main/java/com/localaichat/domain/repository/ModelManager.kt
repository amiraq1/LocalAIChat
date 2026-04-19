package com.localaichat.domain.repository

import com.localaichat.domain.model.ModelManagerState
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.ModelStatus
import kotlinx.coroutines.flow.Flow

interface ModelManager {
    fun observeAvailableModels(): Flow<List<ModelOption>>
    fun observeSelectedModelId(): Flow<String>
    fun observeManagerState(): Flow<ModelManagerState>
    suspend fun getSelectedModelOrNull(): ModelOption?
    suspend fun getSelectedModel(): ModelOption
    suspend fun selectModel(modelId: String)
    suspend fun prepareSelectedModel(): ModelOption
    suspend fun updateModelStatus(modelId: String, status: ModelStatus)
    suspend fun markModelFailed(modelId: String, reason: String)
    suspend fun resetModel(modelId: String)
}
