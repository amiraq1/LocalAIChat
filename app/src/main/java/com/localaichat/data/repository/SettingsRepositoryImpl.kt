package com.localaichat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observeBackendType(): Flow<BackendType> =
        dataStore.data.map { preferences ->
            val name = preferences[PreferenceKeys.BackendType] ?: BackendType.FAKE.name
            try {
                BackendType.valueOf(name)
            } catch (_: IllegalArgumentException) {
                BackendType.FAKE
            }
        }

    override suspend fun getBackendType(): BackendType = observeBackendType().first()

    override suspend fun updateBackendType(type: BackendType) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.BackendType] = type.name
        }
    }

    override fun observeGenerationConfig(): Flow<GenerationConfig> =
        dataStore.data.map { preferences ->
            GenerationConfig(
                maxTokens = preferences[PreferenceKeys.MaxTokens] ?: 512,
                temperature = preferences[PreferenceKeys.Temperature] ?: 0.7f,
            )
        }

    override suspend fun getGenerationConfig(): GenerationConfig = observeGenerationConfig().first()

    override suspend fun updateMaxTokens(value: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.MaxTokens] = value
        }
    }

    override suspend fun updateTemperature(value: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.Temperature] = value
        }
    }
}
