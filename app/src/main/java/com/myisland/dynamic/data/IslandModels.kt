package com.myisland.dynamic.data

import android.graphics.Bitmap

enum class IslandMode {
    HIDDEN,
    COMPACT,
    EXPANDED,
    TOAST
}

enum class IslandContentType {
    NONE,
    MEDIA_PLAYER,
    NOTIFICATION,
    CHARGING,
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
    val modeName: String = "Normal", // Silent, Vibrate, Normal
    val iconResId: Int = 0
)

data class IslandConfig(
    val yOffsetDp: Int = 12,           // Motorola Edge 60 Pro Top Punch Hole Offset
    val xOffsetDp: Int = 0,            // Center Aligned
    val compactWidthDp: Int = 190,
    val compactHeightDp: Int = 38,
    val expandedWidthDp: Int = 350,
    val expandedHeightDp: Int = 170,
    val cornerRadiusDp: Int = 24,
    val isMusicEnabled: Boolean = true,
    val isChargingEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val autoCollapseSeconds: Int = 4
)
