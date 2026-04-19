package com.localaichat.ui.screens.model

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localaichat.domain.model.LocalModel
import com.localaichat.domain.model.LocalModelOperationStage
import com.localaichat.domain.model.LocalModelState
import com.localaichat.ui.theme.DeepCharcoal
import com.localaichat.ui.theme.ElectricViolet
import com.localaichat.ui.theme.ErrorRed
import com.localaichat.ui.theme.GhostWhite
import com.localaichat.ui.theme.MidCharcoal
import com.localaichat.ui.theme.MutedGray
import com.localaichat.ui.theme.NeonCyan
import com.localaichat.ui.theme.NeonCyanDim
import com.localaichat.ui.theme.PureWhite
import com.localaichat.ui.theme.SoftWhite
import com.localaichat.ui.theme.SubtleGray
import com.localaichat.ui.theme.SuccessGreen
import com.localaichat.ui.theme.VoidBlack
import com.localaichat.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        containerColor = VoidBlack,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepCharcoal.copy(alpha = 0.95f),
                    titleContentColor = PureWhite,
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Memory,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Models",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .background(VoidBlack)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 16.dp, bottom = 24.dp
            ),
        ) {
            item {
                Text(
                    text = "Local Model Registry",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Install, load, and manage on-device AI models.",
                    color = GhostWhite,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            items(uiState.models, key = { it.id }) { model ->
                ModelCard(
                    model = model,
                    isSelected = uiState.selectedModelId == model.id,
                    onSelect = { onModelSelected(model.id) },
                    onInstall = { onInstallModel(model.id) },
                    onCancelInstall = { onCancelInstall(model.id) },
                    onLoad = { onLoadModel(model.id) },
                    onCancelLoad = { onCancelLoad(model.id) },
                    onUnload = {
                        if (model.isSelected) {
                            modelPendingUnloadConfirmation = model
                        } else {
                            onUnloadModel(model.id)
                        }
                    },
                )
            }
        }
    }

    // Unload confirmation dialog
    modelPendingUnloadConfirmation?.let { model ->
        AlertDialog(
            onDismissRequest = { modelPendingUnloadConfirmation = null },
            containerColor = MidCharcoal,
            titleContentColor = PureWhite,
            textContentColor = SoftWhite,
            title = { Text("Unload model?", fontWeight = FontWeight.Bold) },
            text = {
                Text("\"${model.displayName}\" is currently selected. Unloading removes it from memory.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUnloadModel(model.id)
                        modelPendingUnloadConfirmation = null
                    },
                ) {
                    Text("Unload", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { modelPendingUnloadConfirmation = null }) {
                    Text("Cancel", color = NeonCyan)
                }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModelCard(
    model: LocalModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onInstall: () -> Unit,
    onCancelInstall: () -> Unit,
    onLoad: () -> Unit,
    onCancelLoad: () -> Unit,
    onUnload: () -> Unit,
) {
    val stateLabel = model.state.asLabel()
    val progress = model.state.progressFraction()
    val isBusyInstalling = model.state.isInstallInProgress()
    val isBusyLoading = model.state.isLoadInProgress()

    val borderColor by animateColorAsState(
        targetValue = when {
            model.isActive -> SuccessGreen.copy(alpha = 0.6f)
            isSelected -> NeonCyan.copy(alpha = 0.4f)
            else -> SubtleGray.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "cardBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MidCharcoal,
                        DeepCharcoal,
                    )
                )
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ── Header row ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.CheckCircle
                        else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) NeonCyan else GhostWhite,
                        modifier = Modifier.size(22.dp),
                    )
                    Column {
                        Text(
                            text = model.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                        )
                        Text(
                            text = model.sizeBytes.toReadableSize(),
                            style = MaterialTheme.typography.labelSmall,
                            color = GhostWhite,
                        )
                    }
                }

                // Status badge
                val (badgeColor, badgeText) = when {
                    model.isActive -> SuccessGreen to "Active"
                    model.state is LocalModelState.Ready -> NeonCyanDim to "Ready"
                    model.state is LocalModelState.Installed -> ElectricViolet to "Installed"
                    model.state is LocalModelState.Processing -> WarningOrange to stateLabel
                    model.state is LocalModelState.Failed -> ErrorRed to "Failed"
                    else -> GhostWhite to "Not installed"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .border(
                            0.5.dp,
                            badgeColor.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // ── Description ───────────────────────────
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = SoftWhite.copy(alpha = 0.75f),
                lineHeight = 18.sp,
            )

            // ── Progress bar ──────────────────────────
            progress?.let { progressValue ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = NeonCyan,
                        trackColor = SubtleGray,
                        strokeCap = StrokeCap.Round,
                    )
                    Text(
                        text = model.state.asDetail(),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan.copy(alpha = 0.7f),
                    )
                }
            }

            // ── Error message ─────────────────────────
            if (model.state is LocalModelState.Failed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorRed.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = model.state.userMessage,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // ── Action buttons ────────────────────────
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val canInstall = !model.isInstalled && !isBusyInstalling && !isBusyLoading
                val canLoad = model.isInstalled && !isBusyInstalling && !isBusyLoading && model.state !is LocalModelState.Ready
                val canUnload = model.isInstalled && !isBusyInstalling
                val canCancelInstall = isBusyInstalling
                val canCancelLoad = isBusyLoading

                if (canInstall) {
                    ActionChip(
                        label = "Install",
                        icon = Icons.Rounded.CloudDownload,
                        accentColor = NeonCyan,
                        onClick = onInstall,
                    )
                }
                if (canCancelInstall) {
                    ActionChip(
                        label = "Cancel",
                        icon = Icons.Rounded.Close,
                        accentColor = WarningOrange,
                        onClick = onCancelInstall,
                    )
                }
                if (canLoad) {
                    ActionChip(
                        label = "Load",
                        icon = Icons.Rounded.PlayArrow,
                        accentColor = SuccessGreen,
                        onClick = onLoad,
                    )
                }
                if (canCancelLoad) {
                    ActionChip(
                        label = "Cancel",
                        icon = Icons.Rounded.Stop,
                        accentColor = WarningOrange,
                        onClick = onCancelLoad,
                    )
                }
                if (canUnload && (model.state is LocalModelState.Ready || model.state is LocalModelState.Installed)) {
                    ActionChip(
                        label = "Unload",
                        icon = Icons.Rounded.PowerSettingsNew,
                        accentColor = ErrorRed,
                        onClick = onUnload,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(0.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor,
                fontWeight = FontWeight.SemiBold,
            )
        }
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
    is LocalModelState.Failed -> "Failed"
}

private fun LocalModelState.asDetail(): String = when (this) {
    LocalModelState.NotInstalled -> "Not available on device."
    is LocalModelState.Processing -> when (stage) {
        LocalModelOperationStage.DOWNLOADING -> "Downloading… ${this.progressPercent}%"
        LocalModelOperationStage.INSTALLING -> "Installing… ${this.progressPercent}%"
        LocalModelOperationStage.INITIALIZING -> "Initializing… ${this.progressPercent}%"
        LocalModelOperationStage.LOADING_INTO_MEMORY -> "Loading… ${this.progressPercent}%"
    }
    LocalModelState.Installed -> "Installed — ready to load."
    LocalModelState.Ready -> "Loaded and ready."
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
