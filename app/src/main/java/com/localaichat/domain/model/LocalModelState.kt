package com.localaichat.domain.model

/**
 * Stages of model lifecycle operations.
 */
enum class LocalModelOperationStage {
    DOWNLOADING,
    INSTALLING,
    INITIALIZING,
    LOADING_INTO_MEMORY,
}

sealed interface LocalModelState {
    data object NotInstalled : LocalModelState
    
    /**
     * Active processing stage.
     * @property stage The current lifecycle stage.
     * @property progressPercent 0 to 100 progress.
     */
    data class Processing(
        val stage: LocalModelOperationStage,
        val progressPercent: Int
    ) : LocalModelState
    
    data object Installed : LocalModelState
    data object Ready : LocalModelState
    data class Failed(val userMessage: String) : LocalModelState

    // Deprecated legacy helpers for easier transition
    companion object {
        fun Downloading(progress: Int) = Processing(LocalModelOperationStage.DOWNLOADING, progress)
        fun Installing(progress: Int) = Processing(LocalModelOperationStage.INSTALLING, progress)
        fun Loading(progress: Int) = Processing(LocalModelOperationStage.LOADING_INTO_MEMORY, progress)
    }
}
