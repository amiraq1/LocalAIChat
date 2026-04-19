package com.localaichat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localaichat.domain.model.ChatMessage
import com.localaichat.domain.model.ChatRole
import com.localaichat.ui.theme.DeepCharcoal
import com.localaichat.ui.theme.ElectricViolet
import com.localaichat.ui.theme.ElectricVioletGhost
import com.localaichat.ui.theme.GhostWhite
import com.localaichat.ui.theme.MidCharcoal
import com.localaichat.ui.theme.NeonCyan
import com.localaichat.ui.theme.NeonCyanDim
import com.localaichat.ui.theme.NeonCyanGhost
import com.localaichat.ui.theme.PureWhite
import com.localaichat.ui.theme.SoftWhite
import com.localaichat.ui.theme.SubtleGray
import com.localaichat.ui.theme.VoidBlack

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val isUser = message.role == ChatRole.USER
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.88f),
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.Top,
            ) {
                // AI avatar (left side)
                if (!isUser) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp, top = 4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NeonCyan, NeonCyanDim)
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SmartToy,
                            contentDescription = null,
                            tint = VoidBlack,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                val bubbleShape = if (isUser) {
                    RoundedCornerShape(
                        topStart = 20.dp, topEnd = 4.dp,
                        bottomEnd = 20.dp, bottomStart = 20.dp
                    )
                } else {
                    RoundedCornerShape(
                        topStart = 4.dp, topEnd = 20.dp,
                        bottomEnd = 20.dp, bottomStart = 20.dp
                    )
                }

                val backgroundBrush = if (isUser) {
                    Brush.linearGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.15f),
                            NeonCyanDim.copy(alpha = 0.08f),
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MidCharcoal,
                            SubtleGray.copy(alpha = 0.6f),
                        )
                    )
                }

                val borderColor = if (isUser) {
                    NeonCyan.copy(alpha = 0.25f)
                } else {
                    SubtleGray.copy(alpha = 0.5f)
                }

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(bubbleShape)
                        .background(backgroundBrush)
                        .border(
                            width = 0.5.dp,
                            color = borderColor,
                            shape = bubbleShape
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    if (!isUser) {
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan.copy(alpha = 0.7f),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text(
                        text = message.content.ifBlank { if (isUser) "" else "Thinking..." },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = if (isUser) PureWhite else SoftWhite,
                    )
                }

                // User avatar (right side)
                if (isUser) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp, top = 4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ElectricViolet, ElectricViolet.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = VoidBlack,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}
