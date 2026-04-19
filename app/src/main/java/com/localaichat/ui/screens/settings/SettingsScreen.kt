package com.localaichat.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Token
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localaichat.domain.model.BackendStatus
import com.localaichat.domain.model.BackendType
import com.localaichat.ui.theme.DeepCharcoal
import com.localaichat.ui.theme.ElectricViolet
import com.localaichat.ui.theme.ErrorRed
import com.localaichat.ui.theme.GhostWhite
import com.localaichat.ui.theme.MidCharcoal
import com.localaichat.ui.theme.NeonCyan
import com.localaichat.ui.theme.PureWhite
import com.localaichat.ui.theme.SoftWhite
import com.localaichat.ui.theme.SubtleGray
import com.localaichat.ui.theme.VoidBlack
import com.localaichat.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onMaxTokensChanged: (Int) -> Unit,
    onTemperatureChanged: (Float) -> Unit,
    onBackendSelected: (BackendType) -> Unit,
    onServerUrlChanged: (String) -> Unit = {},
) {
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
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(VoidBlack)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // ── Backend Section ───────────────────────
            SectionHeader(
                title = "Inference Backend",
                subtitle = "Choose the engine for local model inference.",
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.availableBackends.forEach { option ->
                    val isSelected = option.type == uiState.selectedBackend
                    val isUnavailable = option.status is BackendStatus.Unavailable

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.06f)
                                else MidCharcoal
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) NeonCyan.copy(alpha = 0.35f)
                                else SubtleGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = !isUnavailable) { onBackendSelected(option.type) }
                            .padding(16.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Rounded.CheckCircle
                                else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) NeonCyan
                                else if (isUnavailable) GhostWhite.copy(alpha = 0.4f)
                                else GhostWhite,
                                modifier = Modifier.size(22.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isUnavailable) GhostWhite.copy(alpha = 0.5f)
                                    else PureWhite,
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUnavailable) GhostWhite.copy(alpha = 0.3f)
                                    else SoftWhite.copy(alpha = 0.7f),
                                    lineHeight = 18.sp,
                                )
                                if (isUnavailable) {
                                    Text(
                                        text = "Unavailable: ${(option.status as BackendStatus.Unavailable).reason}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ErrorRed.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Server URL (visible when LLAMA_CPP is selected) ──
            AnimatedVisibility(
                visible = uiState.selectedBackend == BackendType.LLAMA_CPP,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                ServerUrlCard(
                    serverUrl = uiState.serverUrl,
                    onServerUrlChanged = onServerUrlChanged,
                )
            }

            // ── Compatibility notice ──────────────────
            uiState.selectedModelCompatibility?.let { compatibility ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricViolet.copy(alpha = 0.08f))
                        .border(
                            0.5.dp,
                            ElectricViolet.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Model Compatibility",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElectricViolet,
                        )
                        Text(
                            text = compatibility,
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftWhite.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            HorizontalDivider(
                color = SubtleGray.copy(alpha = 0.3f),
                thickness = 0.5.dp,
            )

            // ── Generation Settings ───────────────────
            SectionHeader(
                title = "Generation",
                subtitle = "Fine-tune output quality and creativity.",
            )

            // Max Tokens slider
            SettingsSliderCard(
                icon = Icons.Rounded.Token,
                title = "Max Tokens",
                value = uiState.generationConfig.maxTokens.toString(),
                sliderValue = uiState.generationConfig.maxTokens.toFloat(),
                onValueChange = { onMaxTokensChanged(it.toInt()) },
                valueRange = 128f..2048f,
                accentColor = NeonCyan,
            )

            // Temperature slider
            SettingsSliderCard(
                icon = Icons.Rounded.Thermostat,
                title = "Temperature",
                value = "%.2f".format(uiState.generationConfig.temperature),
                sliderValue = uiState.generationConfig.temperature,
                onValueChange = onTemperatureChanged,
                valueRange = 0f..1.5f,
                accentColor = WarningOrange,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Settings are stored locally and shared across all engines.",
                style = MaterialTheme.typography.bodySmall,
                color = GhostWhite.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun ServerUrlCard(
    serverUrl: String,
    onServerUrlChanged: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidCharcoal, DeepCharcoal)
                )
            )
            .border(0.5.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cloud,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Server URL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite,
                )
            }

            Text(
                text = "Enter the full URL of your llama.cpp server's OpenAI-compatible endpoint.",
                style = MaterialTheme.typography.bodySmall,
                color = GhostWhite,
                lineHeight = 18.sp,
            )

            TextField(
                value = serverUrl,
                onValueChange = onServerUrlChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        0.5.dp,
                        SubtleGray.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    ),
                placeholder = {
                    Text(
                        "http://192.168.1.100:8080/v1/chat/completions",
                        color = GhostWhite.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SubtleGray.copy(alpha = 0.3f),
                    unfocusedContainerColor = SubtleGray.copy(alpha = 0.2f),
                    cursorColor = NeonCyan,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = SoftWhite,
                ),
                textStyle = MaterialTheme.typography.bodySmall,
                singleLine = true,
            )

            Text(
                text = "Examples: http://192.168.1.x:8080/v1/chat/completions  •  http://127.0.0.1:8080/v1/chat/completions (Termux)",
                style = MaterialTheme.typography.labelSmall,
                color = GhostWhite.copy(alpha = 0.4f),
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = GhostWhite,
        )
    }
}

@Composable
private fun SettingsSliderCard(
    icon: ImageVector,
    title: String,
    value: String,
    sliderValue: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    accentColor: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidCharcoal, DeepCharcoal)
                )
            )
            .border(0.5.dp, SubtleGray.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                    )
                }
            }

            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = SubtleGray,
                ),
            )
        }
    }
}
