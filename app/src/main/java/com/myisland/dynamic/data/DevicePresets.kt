package com.myisland.dynamic.data

data class DevicePreset(
    val name: String,
    val brand: String,
    val config: IslandConfig
)

object DevicePresets {
    val MOTOROLA_EDGE_60_PRO = DevicePreset(
        name = "Motorola Edge 60 Pro",
        brand = "Motorola",
        config = IslandConfig(
            yOffsetDp = 10,
            xOffsetDp = 0,
            compactWidthDp = 200,
            compactHeightDp = 40,
            expandedWidthDp = 350,
            expandedHeightDp = 170,
            cornerRadiusDp = 24
        )
    )

    val SAMSUNG_S20_FE = DevicePreset(
        name = "Samsung Galaxy S20 FE",
        brand = "Samsung",
        config = IslandConfig(
            yOffsetDp = 8,
            xOffsetDp = 0,
            compactWidthDp = 185,
            compactHeightDp = 38,
            expandedWidthDp = 340,
            expandedHeightDp = 165,
            cornerRadiusDp = 22
        )
    )

    val SAMSUNG_S23_PLUS = DevicePreset(
        name = "Samsung Galaxy S23 Plus",
        brand = "Samsung",
        config = IslandConfig(
            yOffsetDp = 8,
            xOffsetDp = 0,
            compactWidthDp = 180,
            compactHeightDp = 36,
            expandedWidthDp = 345,
            expandedHeightDp = 165,
            cornerRadiusDp = 24
        )
    )

    val MOTOROLA_EDGE_50_ULTRA = DevicePreset(
        name = "Motorola Edge 50 Ultra / 40 Pro",
        brand = "Motorola",
        config = IslandConfig(
            yOffsetDp = 10,
            xOffsetDp = 0,
            compactWidthDp = 190,
            compactHeightDp = 38,
            expandedWidthDp = 350,
            expandedHeightDp = 170,
            cornerRadiusDp = 24
        )
    )

    val GENERIC_PUNCH_HOLE = DevicePreset(
        name = "Generic Center Punch-Hole",
        brand = "Generic",
        config = IslandConfig(
            yOffsetDp = 8,
            xOffsetDp = 0,
            compactWidthDp = 185,
            compactHeightDp = 38,
            expandedWidthDp = 340,
            expandedHeightDp = 165,
            cornerRadiusDp = 22
        )
    )

    val ALL_PRESETS = listOf(
        MOTOROLA_EDGE_60_PRO,
        SAMSUNG_S20_FE,
        SAMSUNG_S23_PLUS,
        MOTOROLA_EDGE_50_ULTRA,
        GENERIC_PUNCH_HOLE
    )
}
