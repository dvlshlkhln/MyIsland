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
        private const val KEY_HAPTIC_LEVEL = "haptic_level"
        private const val KEY_AURA_GLOW = "aura_glow"
        private const val KEY_SERVICE_RUNNING = "service_running"
        private const val KEY_MUTED_PACKAGES = "muted_packages"
        private const val KEY_AUTO_CUTOUT_ENABLED = "auto_cutout_enabled"

        private const val KEY_HAS_CUSTOM_PRESET = "has_custom_preset"
        private const val KEY_CUSTOM_Y = "custom_y"
        private const val KEY_CUSTOM_X = "custom_x"
        private const val KEY_CUSTOM_W = "custom_w"
        private const val KEY_CUSTOM_H = "custom_h"
        private const val KEY_CUSTOM_R = "custom_r"
    }

    fun getConfig(): IslandConfig {
        val themeOrdinal = prefs.getInt(KEY_THEME_STYLE, IslandThemeStyle.MIDNIGHT_OLED.ordinal)
        val visOrdinal = prefs.getInt(KEY_VISUALIZER_STYLE, VisualizerStyle.FOUR_BARS.ordinal)
        val hapticOrdinal = prefs.getInt(KEY_HAPTIC_LEVEL, HapticFeedbackLevel.MEDIUM.ordinal)
        val auraOrdinal = prefs.getInt(KEY_AURA_GLOW, AuraGlowIntensity.VIBRANT.ordinal)

        return IslandConfig(
            yOffsetDp = prefs.getInt(KEY_Y_OFFSET, 10),
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
            isAutoCutoutDetectionEnabled = prefs.getBoolean(KEY_AUTO_CUTOUT_ENABLED, false),
            autoCollapseSeconds = prefs.getInt(KEY_AUTO_COLLAPSE_SEC, 4),
            hapticLevel = HapticFeedbackLevel.values().getOrElse(hapticOrdinal) { HapticFeedbackLevel.MEDIUM },
            auraGlowIntensity = AuraGlowIntensity.values().getOrElse(auraOrdinal) { AuraGlowIntensity.VIBRANT },
            hasCustomPreset = prefs.getBoolean(KEY_HAS_CUSTOM_PRESET, false),
            customYOffsetDp = prefs.getInt(KEY_CUSTOM_Y, 10),
            customXOffsetDp = prefs.getInt(KEY_CUSTOM_X, 0),
            customCompactWidthDp = prefs.getInt(KEY_CUSTOM_W, 200),
            customCompactHeightDp = prefs.getInt(KEY_CUSTOM_H, 40),
            customCornerRadiusDp = prefs.getInt(KEY_CUSTOM_R, 24)
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
            putBoolean(KEY_AUTO_CUTOUT_ENABLED, config.isAutoCutoutDetectionEnabled)
            putInt(KEY_AUTO_COLLAPSE_SEC, config.autoCollapseSeconds)
            putInt(KEY_HAPTIC_LEVEL, config.hapticLevel.ordinal)
            putInt(KEY_AURA_GLOW, config.auraGlowIntensity.ordinal)
            apply()
        }
    }

    fun saveCustomPreset(config: IslandConfig) {
        prefs.edit().apply {
            putBoolean(KEY_HAS_CUSTOM_PRESET, true)
            putInt(KEY_CUSTOM_Y, config.yOffsetDp)
            putInt(KEY_CUSTOM_X, config.xOffsetDp)
            putInt(KEY_CUSTOM_W, config.compactWidthDp)
            putInt(KEY_CUSTOM_H, config.compactHeightDp)
            putInt(KEY_CUSTOM_R, config.cornerRadiusDp)
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

    fun setMutedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_MUTED_PACKAGES, packages).apply()
    }
}
