package com.myisland.dynamic.ui.overlay

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.myisland.dynamic.data.*
import com.myisland.dynamic.ui.theme.PureBlack

@Composable
fun DynamicIslandView(
    mode: IslandMode,
    config: IslandConfig,
    mediaState: MediaState,
    notification: NotificationItem?,
    chargingState: ChargingState,
    onToggleExpand: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onDismiss: () -> Unit
) {
    val targetWidth = when (mode) {
        IslandMode.HIDDEN -> 0.dp
        IslandMode.COMPACT -> config.compactWidthDp.dp
        IslandMode.EXPANDED -> config.expandedWidthDp.dp
        IslandMode.TOAST -> (config.expandedWidthDp - 20).dp
    }

    val targetHeight = when (mode) {
        IslandMode.HIDDEN -> 0.dp
        IslandMode.COMPACT -> config.compactHeightDp.dp
        IslandMode.EXPANDED -> config.expandedHeightDp.dp
        IslandMode.TOAST -> (config.compactHeightDp + 16).dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = config.yOffsetDp.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (mode != IslandMode.HIDDEN) {
            Box(
                modifier = Modifier
                    .offset(x = config.xOffsetDp.dp)
                    .width(targetWidth)
                    .height(targetHeight)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(config.cornerRadiusDp.dp))
                    .clip(RoundedCornerShape(config.cornerRadiusDp.dp))
                    .background(PureBlack)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .clickable { onToggleExpand() }
            ) {
                when (mode) {
                    IslandMode.COMPACT -> {
                        CompactPillContent(
                            mediaState = mediaState,
                            notification = notification,
                            chargingState = chargingState
                        )
                    }
                    IslandMode.EXPANDED -> {
                        ExpandedCardContent(
                            mediaState = mediaState,
                            notification = notification,
                            chargingState = chargingState,
                            onPlayPauseToggle = onPlayPauseToggle,
                            onSkipNext = onSkipNext,
                            onSkipPrevious = onSkipPrevious,
                            onDismiss = onDismiss
                        )
                    }
                    IslandMode.TOAST -> {
                        ExpandedChargingView(chargingState = chargingState)
                    }
                    else -> {}
                }
            }
        }
    }
}
