package com.myisland.dynamic.ui.overlay

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    navigationState: NavigationState = NavigationState(),
    downloadState: DownloadState = DownloadState(),
    recordingState: RecordingState = RecordingState(),
    hotspotState: HotspotState = HotspotState(),
    audioOutputManager: com.myisland.dynamic.utils.AudioOutputManager? = null,
    isCalibrationMode: Boolean = false,
    onPositionDragged: (Int, Int) -> Unit = { _, _ -> },
    onToggleExpand: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit = {},
    onDismiss: () -> Unit,
    onSwipeLeftDismiss: () -> Unit = {},
    onEndCall: () -> Unit = {},
    onToggleMuteCall: () -> Unit = {},
    onAddTimerMinute: () -> Unit = {},
    onToggleTorch: () -> Unit = {},
    onToggleAudioOutput: () -> Unit = {},
    onMuteActiveApp: () -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val hapticManager = remember(config.hapticLevel) { HapticManager(context, config.hapticLevel) }

    var showQuickActionMenu by remember { mutableStateOf(false) }

    val isDualSession = (mediaState.isPlaying || mediaState.albumArt != null) && timerState.isRunning && mode == IslandMode.COMPACT

    val paletteColors = remember(mediaState.albumArt, notification?.icon) {
        PaletteThemeExtractor.extractColors(mediaState.albumArt ?: notification?.icon)
    }

    var dragXOffset by remember { mutableFloatStateOf(0f) }
    val animatedDragX by animateFloatAsState(
        targetValue = dragXOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dragX"
    )

    val targetWidth = when (mode) {
        IslandMode.HIDDEN -> 0.dp
        IslandMode.COMPACT -> if (isDualSession) (config.compactWidthDp - 40).dp else config.compactWidthDp.dp
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

    val themeBackgroundModifier = when (config.themeStyle) {
        IslandThemeStyle.MIDNIGHT_OLED -> Modifier.background(PureBlack)
        IslandThemeStyle.CYBERPUNK_NEON -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFF0F0C20), Color(0xFF2D114C)))
        )
        IslandThemeStyle.SUNSET_GOLD -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFF1F1000), Color(0xFF381C00)))
        )
        IslandThemeStyle.GLASSMORPHISM -> Modifier.background(Color(0xCC111118))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mode != IslandMode.HIDDEN) {
            Box(
                modifier = Modifier
                    .offset(x = (config.xOffsetDp + (animatedDragX / density)).dp)
                    .width(targetWidth)
                    .height(targetHeight)
                    .alpha(alphaFraction)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(config.cornerRadiusDp.dp),
                        spotColor = if (isCalibrationMode) Color(0xFFFF7675) else if (navigationState.isNavigating) Color(0xFF00CEC9) else paletteColors.vibrantAccent,
                        ambientColor = if (isCalibrationMode) Color(0xFFFF7675) else if (navigationState.isNavigating) Color(0xFF00CEC9) else paletteColors.vibrantAccent
                    )
                    .clip(RoundedCornerShape(config.cornerRadiusDp.dp))
                    .then(themeBackgroundModifier)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                    .pointerInput(isCalibrationMode) {
                        if (isCalibrationMode) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaX = (dragAmount.x / density).toInt()
                                val deltaY = (dragAmount.y / density).toInt()
                                onPositionDragged(config.xOffsetDp + deltaX, config.yOffsetDp + deltaY)
                            }
                        } else {
                            detectTapGestures(
                                onTap = {
                                    hapticManager.performClickHaptic()
                                    showQuickActionMenu = false
                                    onToggleExpand()
                                },
                                onDoubleTap = {
                                    hapticManager.performHeavyHaptic()
                                    if (mediaState.isPlaying || mediaState.albumArt != null) {
                                        onPlayPauseToggle()
                                    } else {
                                        onMuteActiveApp()
                                    }
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
                    }
                    .pointerInput(isCalibrationMode) {
                        if (!isCalibrationMode) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (dragXOffset < -100f) {
                                        hapticManager.performHeavyHaptic()
                                        onSwipeLeftDismiss()
                                    } else if (dragXOffset > 100f && mediaState.isPlaying) {
                                        hapticManager.performClickHaptic()
                                        onSkipNext()
                                    }
                                    dragXOffset = 0f
                                },
                                onDragCancel = { dragXOffset = 0f },
                                onHorizontalDrag = { _, dragAmount ->
                                    dragXOffset = (dragXOffset + dragAmount).coerceIn(-300f, 300f)
                                }
                            )
                        }
                    }
            ) {
                if (isCalibrationMode) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Drag To Align • Y:${config.yOffsetDp}dp",
                            color = Color(0xFFFF7675),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
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
                                navigationState = navigationState,
                                downloadState = downloadState,
                                recordingState = recordingState,
                                hotspotState = hotspotState,
                                visualizerStyle = config.visualizerStyle,
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
                                navigationState = navigationState,
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
                                onSeekTo = onSeekTo,
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
                                },
                                onToggleTorch = onToggleTorch,
                                onToggleAudioOutput = onToggleAudioOutput,
                                onMuteActiveApp = onMuteActiveApp
                            )
                        }
                        IslandMode.TOAST -> {
                            ExpandedChargingView(chargingState = chargingState)
                        }
                        else -> {}
                    }
                }
            }

            if (isDualSession) {
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(elevation = 12.dp, shape = CircleShape, spotColor = Color(0xFFFDCB6E))
                        .clip(CircleShape)
                        .background(PureBlack)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    hapticManager.performClickHaptic()
                                    onToggleExpand()
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val mins = timerState.remainingSeconds / 60
                    Text(
                        text = String.format("%02d", mins),
                        color = Color(0xFFFDCB6E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
