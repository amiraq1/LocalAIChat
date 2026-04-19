package com.localaichat.domain.repository

import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeBackendType(): Flow<BackendType>
    suspend fun getBackendType(): BackendType
    suspend fun updateBackendType(type: BackendType)

    fun observeGenerationConfig(): Flow<GenerationConfig>
    suspend fun getGenerationConfig(): GenerationConfig
    suspend fun updateMaxTokens(value: Int)
    suspend fun updateTemperature(value: Float)

    fun observeServerUrl(): Flow<String>
    suspend fun getServerUrl(): String
    suspend fun updateServerUrl(url: String)
}
