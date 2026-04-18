package com.localaichat.ui.screens.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localaichat.domain.model.LocalModel
import com.localaichat.domain.model.LocalModelOperationStage
import com.localaichat.domain.model.LocalModelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelScreen(
    uiState: ModelUiState,
    onModelSelected: (String) -> Unit,
    onInstallModel: (String) -> Unit,
    onCancelInstall: (String) -> Unit,
    onLoadModel: (String) -> Unit,
    onCancelLoad: (String) -> Unit,
    onUnloadModel: (String) -> Unit,
) {
    var modelPendingUnloadConfirmation by remember { mutableStateOf<LocalModel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Local Models") })
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Local model registry",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Local models can be installed, loaded into memory, selected for chat, or unloaded. The workflow is placeholder-only for now.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(uiState.models, key = { it.id }) { model ->
                val stateLabel = model.state.asLabel()
                val stateDetail = model.state.asDetail()
                val progress = model.state.progressFraction()
                val isBusyInstalling = model.state.isInstallInProgress()
                val isBusyLoading = model.state.isLoadInProgress()
                val canInstall = !model.isInstalled && !isBusyInstalling && !isBusyLoading
                val canCancelInstall = isBusyInstalling
                val canLoad = model.isInstalled && !isBusyInstalling && !isBusyLoading && model.state !is LocalModelState.Ready
                val canCancelLoad = isBusyLoading
                val canUnload = model.isInstalled && !isBusyInstalling
                val canSelect = !isBusyInstalling && !isBusyLoading

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onModelSelected(model.id) },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(
                            selected = uiState.selectedModelId == model.id,
                            onClick = { onModelSelected(model.id) },
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(model.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(model.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Size ${model.sizeBytes.toReadableSize()} | Path ${model.localPath ?: "Unassigned"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = stateDetail,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (model.state is LocalModelState.Failed) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(stateLabel) },
                                )
                                if (model.isSelected) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Selected") },
                                    )
                                }
                                if (model.isActive) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Active") },
                                    )
                                }
                            }
                            progress?.let { progressValue ->
                                LinearProgressIndicator(
                                    progress = { progressValue },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = { onInstallModel(model.id) },
                                    enabled = canInstall,
                                ) {
                                    Text("Install")
                                }
                                OutlinedButton(
                                    onClick = { onCancelInstall(model.id) },
                                    enabled = canCancelInstall,
                                ) {
                                    Text("Cancel Install")
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = { onLoadModel(model.id) },
                                    enabled = canLoad,
                                ) {
                                    Text("Load")
                                }
                                OutlinedButton(
                                    onClick = { onCancelLoad(model.id) },
                                    enabled = canCancelLoad,
                                ) {
                                    Text("Cancel Load")
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (model.isSelected) {
                                            modelPendingUnloadConfirmation = model
                                        } else {
                                            onUnloadModel(model.id)
                                        }
                                    },
                                    enabled = canUnload,
                                ) {
                                    Text("Unload")
                                }
                                OutlinedButton(
                                    onClick = { onModelSelected(model.id) },
                                    enabled = canSelect,
                                ) {
                                    Text("Select")
                                }
                            }
                            if (model.state is LocalModelState.Failed) {
                                Text(
                                    text = model.state.userMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    modelPendingUnloadConfirmation?.let { model ->
        AlertDialog(
            onDismissRequest = { modelPendingUnloadConfirmation = null },
            title = { Text("Unload selected model?") },
            text = {
                Text("`${model.displayName}` is currently selected. Unloading it will remove the active local model from memory.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUnloadModel(model.id)
                        modelPendingUnloadConfirmation = null
                    },
                ) {
                    Text("Unload")
                }
            },
            dismissButton = {
                TextButton(onClick = { modelPendingUnloadConfirmation = null }) {
                    Text("Keep loaded")
                }
            },
        )
    }
}

private fun Long.toReadableSize(): String = when {
    this >= 1024L * 1024L * 1024L -> String.format("%.1f GB", this / (1024f * 1024f * 1024f))
    this >= 1024L * 1024L -> String.format("%.0f MB", this / (1024f * 1024f))
    this >= 1024L -> String.format("%.0f KB", this / 1024f)
    else -> "$this B"
}

private fun LocalModelState.asLabel(): String = when (this) {
    LocalModelState.NotInstalled -> "Not installed"
    is LocalModelState.Processing -> when (stage) {
        LocalModelOperationStage.DOWNLOADING -> "Downloading"
        LocalModelOperationStage.INSTALLING -> "Installing"
        LocalModelOperationStage.INITIALIZING -> "Initializing"
        LocalModelOperationStage.LOADING_INTO_MEMORY -> "Loading"
    }
    LocalModelState.Installed -> "Installed"
    LocalModelState.Ready -> "Ready"
    is LocalModelState.Failed -> "Action failed"
}

private fun LocalModelState.asDetail(): String = when (this) {
    LocalModelState.NotInstalled -> "This model is not available on the device yet."
    is LocalModelState.Processing -> when (stage) {
        LocalModelOperationStage.DOWNLOADING -> "Downloading model files: ${this.progressPercent}%."
        LocalModelOperationStage.INSTALLING -> "Preparing local files: ${this.progressPercent}%."
        LocalModelOperationStage.INITIALIZING -> "Initializing backend runtime: ${this.progressPercent}%."
        LocalModelOperationStage.LOADING_INTO_MEMORY -> "Loading model into memory: ${this.progressPercent}%."
    }
    LocalModelState.Installed -> "Installed on device and ready to load."
    LocalModelState.Ready -> "Loaded into memory and ready for selection."
    is LocalModelState.Failed -> this.userMessage
}

private fun LocalModelState.progressFraction(): Float? = when (this) {
    is LocalModelState.Processing -> this.progressPercent / 100f
    else -> null
}

private fun LocalModelState.isInstallInProgress(): Boolean = this is LocalModelState.Processing &&
    (stage == LocalModelOperationStage.DOWNLOADING || stage == LocalModelOperationStage.INSTALLING)

private fun LocalModelState.isLoadInProgress(): Boolean = this is LocalModelState.Processing &&
    (stage == LocalModelOperationStage.INITIALIZING || stage == LocalModelOperationStage.LOADING_INTO_MEMORY)
