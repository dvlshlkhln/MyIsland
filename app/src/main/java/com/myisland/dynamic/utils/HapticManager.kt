package com.myisland.dynamic.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.myisland.dynamic.data.HapticFeedbackLevel

class HapticManager(context: Context, var hapticLevel: HapticFeedbackLevel = HapticFeedbackLevel.MEDIUM) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun performClickHaptic() {
        if (hapticLevel == HapticFeedbackLevel.OFF || vibrator?.hasVibrator() != true) return
        val duration = when (hapticLevel) {
            HapticFeedbackLevel.LIGHT -> 8L
            HapticFeedbackLevel.MEDIUM -> 15L
            HapticFeedbackLevel.HEAVY -> 25L
            HapticFeedbackLevel.OFF -> 0L
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && hapticLevel == HapticFeedbackLevel.MEDIUM) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    fun performHeavyHaptic() {
        if (hapticLevel == HapticFeedbackLevel.OFF || vibrator?.hasVibrator() != true) return
        val duration = when (hapticLevel) {
            HapticFeedbackLevel.LIGHT -> 15L
            HapticFeedbackLevel.MEDIUM -> 35L
            HapticFeedbackLevel.HEAVY -> 50L
            HapticFeedbackLevel.OFF -> 0L
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && hapticLevel == HapticFeedbackLevel.MEDIUM) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}
