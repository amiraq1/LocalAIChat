package com.localaichat.domain.model

data class ModelManagerState(
    val selectedModelId: String = "",
    val selectedModelName: String? = null,
    val activeModelId: String? = null,
    val activeModelName: String? = null,
    val selectedModelStatus: ModelStatus = ModelStatus.NotLoaded,
    val selectedModelFailureMessage: String? = null,
    val selectedModelAvailabilityMessage: String = "Select a local model to start chatting.",
) {
    val hasSelectedModel: Boolean
        get() = selectedModelId.isNotBlank()

    val isSelectedModelReady: Boolean
        get() = selectedModelStatus == ModelStatus.Ready
}
