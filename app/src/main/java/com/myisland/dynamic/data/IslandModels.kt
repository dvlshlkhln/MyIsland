package com.myisland.dynamic.data

import android.graphics.Bitmap

enum class IslandMode {
    HIDDEN,
    COMPACT,
    EXPANDED,
    TOAST
}

enum class CutoutShape {
    SINGLE_HOLE,
    PILL,
    DUAL_HOLE,
    CORNER_NOTCH
}

enum class IslandThemeStyle {
    MIDNIGHT_OLED,
    CYBERPUNK_NEON,
    SUNSET_GOLD,
    GLASSMORPHISM
}

enum class VisualizerStyle {
    FOUR_BARS,
    WAVEFORM,
    PULSE_RING
}

enum class HapticFeedbackLevel {
    OFF,
    LIGHT,
    MEDIUM,
    HEAVY
}

enum class NavigationDirection {
    STRAIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    U_TURN,
    ARRIVE
}

data class NavigationState(
    val isNavigating: Boolean = false,
    val direction: NavigationDirection = NavigationDirection.STRAIGHT,
    val distanceText: String = "",
    val streetName: String = "",
    val appName: String = "Google Maps"
)

data class MediaState(
    val title: String = "",
    val artist: String = "",
    val albumArt: Bitmap? = null,
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val packageName: String = ""
)

data class CallState(
    val callerName: String = "",
    val callerNumber: String = "",
    val avatar: Bitmap? = null,
    val isRinging: Boolean = false,
    val isActiveCall: Boolean = false,
    val callDurationSeconds: Int = 0,
    val isMuted: Boolean = false
)

data class TimerState(
    val title: String = "Timer",
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isRunning: Boolean = false
)

data class BluetoothDeviceState(
    val deviceName: String = "",
    val isConnected: Boolean = false,
    val batteryLevel: Int = -1
)

data class NotificationItem(
    val id: String = "",
    val packageName: String = "",
    val appName: String = "",
    val title: String = "",
    val message: String = "",
    val icon: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChargingState(
    val isCharging: Boolean = false,
    val batteryLevel: Int = 100,
    val isFastCharging: Boolean = false,
    val chargingWattText: String = "68W TurboPower"
)

enum class AudioDeviceType {
    SPEAKER,
    BLUETOOTH,
    HEADPHONES
}

data class AudioOutputDevice(
    val id: String = "",
    val name: String = "Phone Speaker",
    val type: AudioDeviceType = AudioDeviceType.SPEAKER,
    val isActive: Boolean = false
)

data class DownloadState(
    val isDownloading: Boolean = false,
    val fileName: String = "",
    val progressPercent: Int = 0,
    val bytesPerSec: Long = 0L,
    val appName: String = "Download Manager"
)

data class RecordingState(
    val isRecording: Boolean = false,
    val type: String = "Screen",
    val durationSeconds: Int = 0,
    val isPaused: Boolean = false
)

data class HotspotState(
    val isActive: Boolean = false,
    val clientCount: Int = 0,
    val speedKbps: Long = 0L
)

data class RingerState(
    val modeName: String = "Normal",
    val iconResId: Int = 0
)

data class IslandConfig(
    val yOffsetDp: Int = 10,           // Motorola Edge 60 Pro Default
    val xOffsetDp: Int = 0,            // Center Aligned
    val compactWidthDp: Int = 200,
    val compactHeightDp: Int = 40,
    val expandedWidthDp: Int = 350,
    val expandedHeightDp: Int = 170,
    val cornerRadiusDp: Int = 24,
    val cutoutShape: CutoutShape = CutoutShape.SINGLE_HOLE,
    val themeStyle: IslandThemeStyle = IslandThemeStyle.MIDNIGHT_OLED,
    val visualizerStyle: VisualizerStyle = VisualizerStyle.FOUR_BARS,
    val showOnLockscreen: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isChargingEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isCallsEnabled: Boolean = true,
    val isTimersEnabled: Boolean = true,
    val isBluetoothEnabled: Boolean = true,
    val isVolumeHudEnabled: Boolean = true,
    val isAutoCutoutDetectionEnabled: Boolean = false,
    val autoCollapseSeconds: Int = 4,
    val hapticLevel: HapticFeedbackLevel = HapticFeedbackLevel.MEDIUM,
    val isCalibrationMode: Boolean = false
)
