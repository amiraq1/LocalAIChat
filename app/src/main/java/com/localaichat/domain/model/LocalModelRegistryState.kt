package com.localaichat.domain.model

data class LocalModelRegistryState(
    val models: List<LocalModel> = emptyList(),
    val selectedModelId: String = "",
    val activeModelId: String? = null,
)
