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

enum class IslandContentType {
    NONE,
    MEDIA_PLAYER,
    CALL,
    TIMER,
    NOTIFICATION,
    CHARGING,
    BLUETOOTH_BANNER,
    VOLUME_RINGER,
    RINGER_MODE
}

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
    val isFastCharging: Boolean = false
)

data class RingerState(
    val modeName: String = "Normal",
    val iconResId: Int = 0
)

data class IslandConfig(
    val yOffsetDp: Int = 12,           // Motorola Edge 60 Pro Default
    val xOffsetDp: Int = 0,            // Center Aligned
    val compactWidthDp: Int = 190,
    val compactHeightDp: Int = 38,
    val expandedWidthDp: Int = 350,
    val expandedHeightDp: Int = 170,
    val cornerRadiusDp: Int = 24,
    val cutoutShape: CutoutShape = CutoutShape.SINGLE_HOLE,
    val showOnLockscreen: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isChargingEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isCallsEnabled: Boolean = true,
    val isTimersEnabled: Boolean = true,
    val isBluetoothEnabled: Boolean = true,
    val isVolumeHudEnabled: Boolean = true,
    val autoCollapseSeconds: Int = 4
)
