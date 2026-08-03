package com.myisland.dynamic.utils

import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.WindowInsets

data class CutoutDimensions(
    val yOffsetDp: Int = 10,
    val compactWidthDp: Int = 200,
    val compactHeightDp: Int = 40,
    val hasCutout: Boolean = false
)

object CutoutDetector {

    fun detectCutout(view: View): CutoutDimensions {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return CutoutDimensions()
        }

        val insets: WindowInsets? = view.rootWindowInsets
        val cutout: DisplayCutout? = insets?.displayCutout

        if (cutout != null) {
            val density = view.resources.displayMetrics.density
            val boundingRects = cutout.boundingRects

            if (boundingRects.isNotEmpty()) {
                val rect = boundingRects[0]
                // Top offset calculation based on cutout center
                val cutoutCenterYPx = rect.top + (rect.height() / 2f)
                val calculatedYOffsetDp = (cutoutCenterYPx / density).toInt().coerceAtLeast(4)

                // Compact width calculation with safe padding
                val cutoutWidthDp = (rect.width() / density).toInt()
                val calculatedWidthDp = (cutoutWidthDp + 140).coerceIn(160, 260)
                val calculatedHeightDp = ((rect.height() / density) + 16).toInt().coerceIn(34, 52)

                return CutoutDimensions(
                    yOffsetDp = calculatedYOffsetDp,
                    compactWidthDp = calculatedWidthDp,
                    compactHeightDp = calculatedHeightDp,
                    hasCutout = true
                )
            }

            val safeInsetTopPx = cutout.safeInsetTop
            if (safeInsetTopPx > 0) {
                val density = view.resources.displayMetrics.density
                val topDp = (safeInsetTopPx / density).toInt()
                return CutoutDimensions(
                    yOffsetDp = (topDp / 2).coerceAtLeast(6),
                    compactWidthDp = 200,
                    compactHeightDp = 40,
                    hasCutout = true
                )
            }
        }

        return CutoutDimensions()
    }
}
