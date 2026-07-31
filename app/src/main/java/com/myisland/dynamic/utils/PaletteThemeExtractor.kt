package com.myisland.dynamic.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

data class IslandPaletteColors(
    val vibrantAccent: Color = Color(0xFFA29BFE),
    val dominantGlow: Color = Color(0xFF6C5CE7),
    val mutedBackground: Color = Color(0xFF16161E)
)

object PaletteThemeExtractor {

    fun extractColors(bitmap: Bitmap?): IslandPaletteColors {
        if (bitmap == null) return IslandPaletteColors()

        return try {
            val palette = Palette.from(bitmap).generate()

            val vibrantRgb = palette.getVibrantColor(0xFFA29BFE.toInt())
            val dominantRgb = palette.getDominantColor(0xFF6C5CE7.toInt())
            val mutedRgb = palette.getMutedColor(0xFF16161E.toInt())

            IslandPaletteColors(
                vibrantAccent = Color(vibrantRgb),
                dominantGlow = Color(dominantRgb).copy(alpha = 0.4f),
                mutedBackground = Color(mutedRgb)
            )
        } catch (e: Exception) {
            IslandPaletteColors()
        }
    }
}
