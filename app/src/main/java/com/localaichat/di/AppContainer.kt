package com.localaichat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.localaichat.data.local.LocalAiChatDatabase
import com.localaichat.data.repository.ChatRepositoryImpl
import com.localaichat.data.repository.LocalModelRegistryImpl
import com.localaichat.data.repository.ModelManagerImpl
import com.localaichat.data.repository.PlaceholderLocalModelInstallationWorkflow
import com.localaichat.data.repository.PlaceholderLocalModelLoadingWorkflow
import com.localaichat.data.repository.ModelRepositoryImpl
import com.localaichat.data.repository.SettingsRepositoryImpl
import com.localaichat.data.backend.mediapipe.MediaPipeInferenceAdapter
import com.localaichat.data.backend.real.AndroidBackendContext
import com.localaichat.data.backend.real.AndroidBackendContextProvider
import com.localaichat.data.repository.AdapterLlmEngine
import com.localaichat.data.repository.BackendManagerImpl
import com.localaichat.data.repository.DynamicLlmEngine
import com.localaichat.data.repository.FakeLocalLlmEngine
import com.localaichat.data.repository.ModelCompatibilityCheckerImpl
import com.localaichat.data.repository.TermuxLlmEngine
import com.localaichat.domain.repository.BackendManager
import com.localaichat.domain.repository.ChatRepository
import com.localaichat.domain.repository.LlmEngine
import com.localaichat.domain.repository.LocalModelInstallationWorkflow
import com.localaichat.domain.repository.LocalModelLoadingWorkflow
import com.localaichat.domain.repository.LocalModelRegistry
import com.localaichat.domain.repository.ModelCompatibilityChecker
import com.localaichat.domain.repository.ModelManager
import com.localaichat.domain.repository.ModelRepository
import com.localaichat.domain.repository.SettingsRepository
import com.localaichat.domain.usecase.ObserveBackendOptionsUseCase
import com.localaichat.domain.usecase.ObserveBackendSelectionUseCase
import com.localaichat.domain.usecase.ObserveChatMessagesUseCase
import com.localaichat.domain.usecase.ObserveChatReadinessUseCase
import com.localaichat.domain.usecase.ObserveSettingsUseCase
import com.localaichat.domain.usecase.SelectBackendUseCase
import com.localaichat.domain.usecase.SendMessageUseCase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "local_ai_chat_preferences")

class AppContainer(
    context: Context,
) {
    private val database = Room.databaseBuilder(
        context,
        LocalAiChatDatabase::class.java,
        "local_ai_chat.db",
    ).build()

    private val androidBackendContext: AndroidBackendContext = AndroidBackendContextProvider(context)

    val chatRepository: ChatRepository = ChatRepositoryImpl(database.chatDao())
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(context.dataStore)
    val modelRepository: ModelRepository = ModelRepositoryImpl(context.dataStore)
    val modelCompatibilityChecker: ModelCompatibilityChecker = ModelCompatibilityCheckerImpl()
    val modelManager: ModelManager = ModelManagerImpl(
        modelRepository = modelRepository,
        settingsRepository = settingsRepository,
        compatibilityChecker = modelCompatibilityChecker,
    )

    val backendManager: BackendManager = BackendManagerImpl(settingsRepository)

    val localModelInstallationWorkflow: LocalModelInstallationWorkflow = PlaceholderLocalModelInstallationWorkflow()
    val localModelLoadingWorkflow: LocalModelLoadingWorkflow = PlaceholderLocalModelLoadingWorkflow()
    val localModelRegistry: LocalModelRegistry = LocalModelRegistryImpl(
        modelManager = modelManager,
        installationWorkflow = localModelInstallationWorkflow,
        loadingWorkflow = localModelLoadingWorkflow,
    )

    // ── Engines ───────────────────────────────────────
    private val fakeEngine: LlmEngine = FakeLocalLlmEngine()

    private val llamaCppEngine: LlmEngine = TermuxLlmEngine(
        settingsRepository = settingsRepository,
    )

    private val mediaPipeLlmEngine: LlmEngine = AdapterLlmEngine(
        adapter = MediaPipeInferenceAdapter(androidBackendContext)
    )

    val llmEngine: LlmEngine = DynamicLlmEngine(
        settingsRepository = settingsRepository,
        backendManager = backendManager,
        fakeEngine = fakeEngine,
        mediaPipeEngine = mediaPipeLlmEngine,
        llamaCppEngine = llamaCppEngine,
    )

    val observeChatMessagesUseCase = ObserveChatMessagesUseCase(chatRepository)
    val observeSettingsUseCase = ObserveSettingsUseCase(
        settingsRepository = settingsRepository,
        modelRepository = modelRepository,
        backendManager = backendManager,
        modelManager = modelManager,
    )
    val observeBackendOptionsUseCase = ObserveBackendOptionsUseCase(backendManager)
    val observeBackendSelectionUseCase = ObserveBackendSelectionUseCase(backendManager)
    val selectBackendUseCase = SelectBackendUseCase(backendManager)
    val observeChatReadinessUseCase = ObserveChatReadinessUseCase(
        backendManager = backendManager,
        modelManager = modelManager,
    )

    val sendMessageUseCase = SendMessageUseCase(
        chatRepository = chatRepository,
        settingsRepository = settingsRepository,
        modelManager = modelManager,
        llmEngine = llmEngine,
    )
}
