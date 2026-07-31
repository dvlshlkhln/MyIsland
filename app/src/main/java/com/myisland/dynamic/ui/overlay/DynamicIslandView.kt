package com.myisland.dynamic.ui.overlay

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.myisland.dynamic.data.*
import com.myisland.dynamic.service.VolumeRingerState
import com.myisland.dynamic.ui.theme.PureBlack
import com.myisland.dynamic.utils.HapticManager
import com.myisland.dynamic.utils.PaletteThemeExtractor

@Composable
fun DynamicIslandView(
    mode: IslandMode,
    config: IslandConfig,
    mediaState: MediaState,
    notification: NotificationItem?,
    chargingState: ChargingState,
    callState: CallState = CallState(),
    timerState: TimerState = TimerState(),
    bluetoothState: BluetoothDeviceState = BluetoothDeviceState(),
    volumeRingerState: VolumeRingerState = VolumeRingerState(),
    onToggleExpand: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onDismiss: () -> Unit,
    onSwipeLeftDismiss: () -> Unit = {},
    onEndCall: () -> Unit = {},
    onToggleMuteCall: () -> Unit = {},
    onAddTimerMinute: () -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val hapticManager = remember { HapticManager(context) }

    var showQuickActionMenu by remember { mutableStateOf(false) }

    // Dynamic Palette Extractor
    val paletteColors = remember(mediaState.albumArt, notification?.icon) {
        PaletteThemeExtractor.extractColors(mediaState.albumArt ?: notification?.icon)
    }

    // Swipe Left Gesture State
    var dragXOffset by remember { mutableFloatStateOf(0f) }
    val animatedDragX by animateFloatAsState(
        targetValue = dragXOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "dragX"
    )

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

    val alphaFraction = ((1f - (kotlin.math.abs(animatedDragX) / 250f))).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = config.yOffsetDp.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (mode != IslandMode.HIDDEN) {
            Box(
                modifier = Modifier
                    .offset(x = (config.xOffsetDp + (animatedDragX / density)).dp)
                    .width(targetWidth)
                    .height(targetHeight)
                    .alpha(alphaFraction)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(config.cornerRadiusDp.dp), spotColor = paletteColors.vibrantAccent)
                    .clip(RoundedCornerShape(config.cornerRadiusDp.dp))
                    .background(PureBlack)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                hapticManager.performClickHaptic()
                                showQuickActionMenu = false
                                onToggleExpand()
                            },
                            onLongPress = {
                                hapticManager.performHeavyHaptic()
                                showQuickActionMenu = true
                                if (mode != IslandMode.EXPANDED) {
                                    onToggleExpand()
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragXOffset < -100f) {
                                    hapticManager.performHeavyHaptic()
                                    onSwipeLeftDismiss()
                                }
                                dragXOffset = 0f
                            },
                            onDragCancel = { dragXOffset = 0f },
                            onHorizontalDrag = { _, dragAmount ->
                                if (dragAmount < 0 || dragXOffset < 0) { // Drag Left
                                    dragXOffset = (dragXOffset + dragAmount).coerceIn(-300f, 0f)
                                }
                            }
                        )
                    }
            ) {
                when (mode) {
                    IslandMode.COMPACT -> {
                        CompactPillContent(
                            mediaState = mediaState,
                            notification = notification,
                            chargingState = chargingState,
                            callState = callState,
                            timerState = timerState,
                            bluetoothState = bluetoothState,
                            volumeRingerState = volumeRingerState,
                            paletteColors = paletteColors
                        )
                    }
                    IslandMode.EXPANDED -> {
                        ExpandedCardContent(
                            mediaState = mediaState,
                            notification = notification,
                            chargingState = chargingState,
                            callState = callState,
                            timerState = timerState,
                            bluetoothState = bluetoothState,
                            paletteColors = paletteColors,
                            showQuickActionMenu = showQuickActionMenu,
                            onPlayPauseToggle = {
                                hapticManager.performClickHaptic()
                                onPlayPauseToggle()
                            },
                            onSkipNext = {
                                hapticManager.performClickHaptic()
                                onSkipNext()
                            },
                            onSkipPrevious = {
                                hapticManager.performClickHaptic()
                                onSkipPrevious()
                            },
                            onDismiss = {
                                hapticManager.performHeavyHaptic()
                                showQuickActionMenu = false
                                onDismiss()
                            },
                            onEndCall = {
                                hapticManager.performHeavyHaptic()
                                onEndCall()
                            },
                            onToggleMuteCall = {
                                hapticManager.performClickHaptic()
                                onToggleMuteCall()
                            },
                            onAddTimerMinute = {
                                hapticManager.performClickHaptic()
                                onAddTimerMinute()
                            }
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
