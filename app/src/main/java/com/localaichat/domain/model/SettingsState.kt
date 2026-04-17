package com.localaichat.domain.model

data class SettingsState(
    val generationConfig: GenerationConfig = GenerationConfig(),
    val selectedModelId: String = DEFAULT_MODEL_ID,
    val selectedBackend: BackendType = BackendType.FAKE,
    val availableBackends: List<BackendOption> = emptyList(),
    val selectedModelCompatibility: String? = null,
)
 {
    companion object {
        const val DEFAULT_MODEL_ID = ""
    }
}
