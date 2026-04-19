package com.localaichat.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localaichat.domain.model.BackendType
import com.localaichat.di.AppContainer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    private val container: AppContainer,
) : AndroidViewModel(application) {

    val uiState = combine(
        container.observeSettingsUseCase(),
        container.settingsRepository.observeServerUrl(),
    ) { state, serverUrl ->
        SettingsUiState(
            generationConfig = state.generationConfig,
            selectedBackend = state.selectedBackend,
            availableBackends = state.availableBackends,
            selectedModelCompatibility = state.selectedModelCompatibility,
            serverUrl = serverUrl,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun selectBackend(type: BackendType) {
        viewModelScope.launch {
            container.backendManager.selectBackend(type)
        }
    }

    fun updateMaxTokens(value: Int) {
        viewModelScope.launch {
            container.settingsRepository.updateMaxTokens(value)
        }
    }

    fun updateTemperature(value: Float) {
        viewModelScope.launch {
            container.settingsRepository.updateTemperature(value)
        }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            container.settingsRepository.updateServerUrl(url)
        }
    }
}
