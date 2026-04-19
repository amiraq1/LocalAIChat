package com.localaichat.ui.screens.settings

import com.localaichat.domain.model.BackendOption
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationConfig

data class SettingsUiState(
    val generationConfig: GenerationConfig = GenerationConfig(),
    val selectedBackend: BackendType = BackendType.LLAMA_CPP,
    val availableBackends: List<BackendOption> = emptyList(),
    val selectedModelCompatibility: String? = null,
    val serverUrl: String = "http://192.168.1.100:8080/v1/chat/completions",
)
