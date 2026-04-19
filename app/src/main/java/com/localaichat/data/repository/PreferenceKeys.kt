package com.localaichat.data.repository

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val MaxTokens = intPreferencesKey("max_tokens")
    val Temperature = floatPreferencesKey("temperature")
    val SelectedModel = stringPreferencesKey("selected_model")
    val BackendType = stringPreferencesKey("backend_type")
    val ServerUrl = stringPreferencesKey("server_url")
}
