package com.localaichat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.ModelStatus
import com.localaichat.domain.model.SettingsState
import com.localaichat.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ModelRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : ModelRepository {
    private val availableModels = listOf(
        ModelOption(
            id = "tiny-local",
            name = "Tiny Local",
            description = "Fast starter model for mobile prototyping and UI iteration.",
            contextWindow = 4096,
            capabilities = "Fast responses, low memory footprint",
            sizeBytes = 512L * 1024L * 1024L,
            localPath = "/data/user/0/com.localaichat/files/models/tiny-local.gguf",
            isInstalled = true,
            status = ModelStatus.NotLoaded,
        ),
        ModelOption(
            id = "balanced-local",
            name = "Balanced Local",
            description = "A placeholder for a future on-device model with stronger reasoning.",
            contextWindow = 8192,
            capabilities = "Balanced speed and quality",
            sizeBytes = 2_200L * 1024L * 1024L,
            localPath = "/data/user/0/com.localaichat/files/models/balanced-local.gguf",
            isInstalled = true,
            status = ModelStatus.NotLoaded,
        ),
        ModelOption(
            id = "mediapipe-gemma",
            name = "MediaPipe Gemma",
            description = "Gemma 2b model in MediaPipe format.",
            contextWindow = 8192,
            capabilities = "Official MediaPipe support",
            sizeBytes = 1_500L * 1024L * 1024L,
            localPath = "/data/user/0/com.localaichat/files/models/gemma.bin",
            isInstalled = true,
            status = ModelStatus.NotLoaded,
        ),
        ModelOption(
            id = "hybrid-runtime",
            name = "Hybrid Runtime",
            description = "Reserved for a later engine that can switch between on-device and remote.",
            contextWindow = 16384,
            capabilities = "Routing-friendly architecture",
            sizeBytes = 4_800L * 1024L * 1024L,
            localPath = "",
            isInstalled = false,
            status = ModelStatus.NotLoaded,
        ),
    )

    override fun observeAvailableModels(): Flow<List<ModelOption>> = flowOf(availableModels)

    override fun observeSelectedModelId(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[PreferenceKeys.SelectedModel] ?: SettingsState.DEFAULT_MODEL_ID
        }

    override suspend fun getSelectedModel(): ModelOption {
        val selectedId = observeSelectedModelId().first()
        return availableModels.firstOrNull { it.id == selectedId } ?: availableModels.first()
    }

    override suspend fun selectModel(modelId: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SelectedModel] = modelId
        }
    }
}
