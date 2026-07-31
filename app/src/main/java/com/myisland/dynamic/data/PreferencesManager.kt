package com.myisland.dynamic.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "myisland_preferences"
        private const val KEY_Y_OFFSET = "y_offset"
        private const val KEY_X_OFFSET = "x_offset"
        private const val KEY_COMPACT_WIDTH = "compact_width"
        private const val KEY_COMPACT_HEIGHT = "compact_height"
        private const val KEY_EXPANDED_WIDTH = "expanded_width"
        private const val KEY_EXPANDED_HEIGHT = "expanded_height"
        private const val KEY_CORNER_RADIUS = "corner_radius"
        private const val KEY_THEME_STYLE = "theme_style"
        private const val KEY_VISUALIZER_STYLE = "visualizer_style"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_CHARGING_ENABLED = "charging_enabled"
        private const val KEY_NOTIFS_ENABLED = "notifs_enabled"
        private const val KEY_AUTO_COLLAPSE_SEC = "auto_collapse_sec"
        private const val KEY_SERVICE_RUNNING = "service_running"
        private const val KEY_MUTED_PACKAGES = "muted_packages"
    }

    fun getConfig(): IslandConfig {
        val themeOrdinal = prefs.getInt(KEY_THEME_STYLE, IslandThemeStyle.MIDNIGHT_OLED.ordinal)
        val visOrdinal = prefs.getInt(KEY_VISUALIZER_STYLE, VisualizerStyle.FOUR_BARS.ordinal)

        return IslandConfig(
            yOffsetDp = prefs.getInt(KEY_Y_OFFSET, 34),
            xOffsetDp = prefs.getInt(KEY_X_OFFSET, 0),
            compactWidthDp = prefs.getInt(KEY_COMPACT_WIDTH, 200),
            compactHeightDp = prefs.getInt(KEY_COMPACT_HEIGHT, 40),
            expandedWidthDp = prefs.getInt(KEY_EXPANDED_WIDTH, 350),
            expandedHeightDp = prefs.getInt(KEY_EXPANDED_HEIGHT, 170),
            cornerRadiusDp = prefs.getInt(KEY_CORNER_RADIUS, 24),
            themeStyle = IslandThemeStyle.values().getOrElse(themeOrdinal) { IslandThemeStyle.MIDNIGHT_OLED },
            visualizerStyle = VisualizerStyle.values().getOrElse(visOrdinal) { VisualizerStyle.FOUR_BARS },
            isMusicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true),
            isChargingEnabled = prefs.getBoolean(KEY_CHARGING_ENABLED, true),
            isNotificationsEnabled = prefs.getBoolean(KEY_NOTIFS_ENABLED, true),
            autoCollapseSeconds = prefs.getInt(KEY_AUTO_COLLAPSE_SEC, 4)
        )
    }

    fun saveConfig(config: IslandConfig) {
        prefs.edit().apply {
            putInt(KEY_Y_OFFSET, config.yOffsetDp)
            putInt(KEY_X_OFFSET, config.xOffsetDp)
            putInt(KEY_COMPACT_WIDTH, config.compactWidthDp)
            putInt(KEY_COMPACT_HEIGHT, config.compactHeightDp)
            putInt(KEY_EXPANDED_WIDTH, config.expandedWidthDp)
            putInt(KEY_EXPANDED_HEIGHT, config.expandedHeightDp)
            putInt(KEY_CORNER_RADIUS, config.cornerRadiusDp)
            putInt(KEY_THEME_STYLE, config.themeStyle.ordinal)
            putInt(KEY_VISUALIZER_STYLE, config.visualizerStyle.ordinal)
            putBoolean(KEY_MUSIC_ENABLED, config.isMusicEnabled)
            putBoolean(KEY_CHARGING_ENABLED, config.isChargingEnabled)
            putBoolean(KEY_NOTIFS_ENABLED, config.isNotificationsEnabled)
            putInt(KEY_AUTO_COLLAPSE_SEC, config.autoCollapseSeconds)
            apply()
        }
    }

    fun isServiceEnabled(): Boolean {
        return prefs.getBoolean(KEY_SERVICE_RUNNING, false)
    }

    fun setServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SERVICE_RUNNING, enabled).apply()
    }

    fun getMutedPackages(): Set<String> {
        return prefs.getStringSet(KEY_MUTED_PACKAGES, emptySet()) ?: emptySet()
    }

    fun toggleMutedPackage(packageName: String) {
        val set = getMutedPackages().toMutableSet()
        if (set.contains(packageName)) {
            set.remove(packageName)
        } else {
            set.add(packageName)
        }
        prefs.edit().putStringSet(KEY_MUTED_PACKAGES, set).apply()
    }
}
