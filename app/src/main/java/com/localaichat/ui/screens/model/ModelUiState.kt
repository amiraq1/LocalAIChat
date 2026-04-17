package com.localaichat.ui.screens.model

import com.localaichat.domain.model.LocalModel
import com.localaichat.domain.model.LocalModelRegistryState

data class ModelUiState(
    val models: List<LocalModel> = emptyList(),
    val selectedModelId: String = "",
    val activeModelId: String? = null,
    val registryState: LocalModelRegistryState = LocalModelRegistryState(),
)
