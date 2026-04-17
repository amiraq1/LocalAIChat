package com.localaichat.ui.screens.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localaichat.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModelViewModel(
    application: Application,
    private val container: AppContainer,
) : AndroidViewModel(application) {
    val uiState = container.localModelRegistry.observeRegistryState().map { registryState ->
        ModelUiState(
            models = registryState.models,
            selectedModelId = registryState.selectedModelId,
            activeModelId = registryState.activeModelId,
            registryState = registryState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModelUiState(),
    )

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.selectModel(modelId)
        }
    }

    fun installModel(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.installModel(modelId)
        }
    }

    fun cancelInstall(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.cancelInstall(modelId)
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.loadModel(modelId)
        }
    }

    fun cancelLoad(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.cancelLoad(modelId)
        }
    }

    fun unloadModel(modelId: String) {
        viewModelScope.launch {
            container.localModelRegistry.unloadModel(modelId)
        }
    }
}
