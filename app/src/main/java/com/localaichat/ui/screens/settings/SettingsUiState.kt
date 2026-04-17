package com.localaichat.ui.screens.settings

import com.localaichat.domain.model.BackendOption
import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.GenerationConfig

data class SettingsUiState(
    val generationConfig: GenerationConfig = GenerationConfig(),
    val selectedBackend: BackendType = BackendType.FAKE,
    val availableBackends: List<BackendOption> = emptyList(),
    val selectedModelCompatibility: String? = null,
)
