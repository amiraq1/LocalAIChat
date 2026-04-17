package com.localaichat.domain.repository

import com.localaichat.domain.model.ModelOption
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun observeAvailableModels(): Flow<List<ModelOption>>
    fun observeSelectedModelId(): Flow<String>
    suspend fun getSelectedModel(): ModelOption
    suspend fun selectModel(modelId: String)
}
