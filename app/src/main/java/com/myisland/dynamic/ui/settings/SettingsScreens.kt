package com.myisland.dynamic.ui.settings

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.myisland.dynamic.data.*
import com.myisland.dynamic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDashboardScreen(
    config: IslandConfig,
    isServiceRunning: Boolean,
    hasOverlayPerm: Boolean,
    hasNotifPerm: Boolean,
    onToggleService: (Boolean) -> Unit,
    onConfigChange: (IslandConfig) -> Unit,
    onRequestOverlayPerm: () -> Unit,
    onRequestNotifPerm: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var appFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Allowed, 2: Blocked
    var showGestureGuide by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    var mutedPackages by remember { mutableStateOf(prefsManager.getMutedPackages()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MyIsland",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Multi-Device Dynamic Island",
                            fontSize = 12.sp,
                            color = IslandAccentSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service Toggle Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dynamic Island Service",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isServiceRunning) "Service is active & floating over cutout" else "Service is turned off",
                            fontSize = 13.sp,
                            color = if (isServiceRunning) VibrantGreen else TextMuted
                        )
                    }
                    Switch(
                        checked = isServiceRunning,
                        onCheckedChange = onToggleService,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = IslandAccentPrimary
                        )
                    )
                }
            }

            // Live Island Visual Preview Card
            Text(
                text = "Live Island Preview",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            LiveIslandPreviewCard(config = config)

            // Permissions Status Section
            Text(
                text = "Required Permissions",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            PermissionCard(
                title = "Display Over Other Apps",
                subtitle = "Required to render the floating Dynamic Island overlay",
                isGranted = hasOverlayPerm,
                onRequest = onRequestOverlayPerm
            )

            PermissionCard(
                title = "Notification & Media Access",
                subtitle = "Required to show music controls and notification pills",
                isGranted = hasNotifPerm,
                onRequest = onRequestNotifPerm
            )

            // Gesture & Shortcuts Onboarding Guide Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGestureGuide = !showGestureGuide },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gesture,
                                contentDescription = "Gestures",
                                tint = IslandAccentSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gesture Control Cheat-Sheet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Icon(
                            imageVector = if (showGestureGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = TextMuted
                        )
                    }

                    AnimatedVisibility(visible = showGestureGuide) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GestureGuideItem(
                                icon = Icons.Default.TouchApp,
                                title = "Single Tap",
                                description = "Expand or collapse active island pill"
                            )
                            GestureGuideItem(
                                icon = Icons.Default.PlayCircle,
                                title = "Double Tap",
                                description = "Toggle Media Play/Pause or dismiss active card"
                            )
                            GestureGuideItem(
                                icon = Icons.Default.FlashOn,
                                title = "Long Press",
                                description = "Open Quick Action menu (Flashlight, Mute App, Audio Settings)"
                            )
                            GestureGuideItem(
                                icon = Icons.Default.SwipeLeft,
                                title = "Swipe Left",
                                description = "Dismiss & clear current notification item"
                            )
                            GestureGuideItem(
                                icon = Icons.Default.SkipNext,
                                title = "Swipe Right",
                                description = "Skip to next music track"
                            )
                        }
                    }
                }
            }

            // Custom Themes & Visualizers
            Text(
                text = "Aesthetic Themes & Spectrum",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Island Color Theme",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IslandThemeStyle.values().forEach { style ->
                            FilterChip(
                                selected = config.themeStyle == style,
                                onClick = { onConfigChange(config.copy(themeStyle = style)) },
                                label = {
                                    Text(
                                        text = style.name.replace("_", " ").take(9),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Music Spectrum Style",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VisualizerStyle.values().forEach { visStyle ->
                            FilterChip(
                                selected = config.visualizerStyle == visStyle,
                                onClick = { onConfigChange(config.copy(visualizerStyle = visStyle)) },
                                label = {
                                    Text(
                                        text = visStyle.name.replace("_", " "),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentSecondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ambient Accent Aura Glow",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AuraGlowIntensity.values().forEach { glow ->
                            FilterChip(
                                selected = config.auraGlowIntensity == glow,
                                onClick = { onConfigChange(config.copy(auraGlowIntensity = glow)) },
                                label = {
                                    Text(text = glow.name, fontSize = 11.sp)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Behavior & Haptics Section
            Text(
                text = "Behavior & Haptics",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Auto-Collapse Duration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How long expanded notifications stay open before returning to compact pill.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 4, 6, 8, 10).forEach { sec ->
                            FilterChip(
                                selected = config.autoCollapseSeconds == sec,
                                onClick = { onConfigChange(config.copy(autoCollapseSeconds = sec)) },
                                label = {
                                    Text(text = "${sec}s", fontSize = 11.sp)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Collapsed Idle Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose between classic compact pill or a circular accent ring around your camera cutout.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IslandCollapseStyle.values().forEach { style ->
                            FilterChip(
                                selected = config.collapseStyle == style,
                                onClick = { onConfigChange(config.copy(collapseStyle = style)) },
                                label = {
                                    Text(
                                        text = if (style == IslandCollapseStyle.PILL) "Classic Pill" else "Camera Ring Accent",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    if (config.collapseStyle == IslandCollapseStyle.CAMERA_RING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CalibrationSlider(
                            label = "Camera Ring Diameter",
                            value = config.cameraRingDiameterDp.toFloat(),
                            range = 36f..72f,
                            onValueChange = { onConfigChange(config.copy(cameraRingDiameterDp = it.toInt())) }
                        )
                        CalibrationSlider(
                            label = "Ring Stroke Thickness",
                            value = config.cameraRingThicknessDp.toFloat(),
                            range = 2f..8f,
                            onValueChange = { onConfigChange(config.copy(cameraRingThicknessDp = it.toInt())) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Haptic Vibration Feedback",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HapticFeedbackLevel.values().forEach { level ->
                            FilterChip(
                                selected = config.hapticLevel == level,
                                onClick = { onConfigChange(config.copy(hapticLevel = level)) },
                                label = {
                                    Text(text = level.name, fontSize = 11.sp)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslandAccentSecondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Quick Device Presets Section
            Text(
                text = "Device Alignment Presets",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1-Tap Preset Calibration",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(
                            onClick = {
                                onConfigChange(
                                    config.copy(
                                        yOffsetDp = 10,
                                        xOffsetDp = 0,
                                        compactWidthDp = 200,
                                        compactHeightDp = 40,
                                        expandedWidthDp = 350,
                                        expandedHeightDp = 170,
                                        cornerRadiusDp = 24
                                    )
                                )
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Reset Default", fontSize = 12.sp, color = VibrantRed)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "Phone",
                                        tint = IslandAccentSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Select Device Preset",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(DarkSurfaceVariant)
                        ) {
                            DevicePresets.ALL_PRESETS.forEach { preset ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = preset.name,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Y: ${preset.config.yOffsetDp}dp | W: ${preset.config.compactWidthDp}dp",
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        expandedDropdown = false
                                        onConfigChange(
                                            config.copy(
                                                yOffsetDp = preset.config.yOffsetDp,
                                                xOffsetDp = preset.config.xOffsetDp,
                                                compactWidthDp = preset.config.compactWidthDp,
                                                compactHeightDp = preset.config.compactHeightDp,
                                                expandedWidthDp = preset.config.expandedWidthDp,
                                                expandedHeightDp = preset.config.expandedHeightDp,
                                                cornerRadiusDp = preset.config.cornerRadiusDp
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom User Alignment Save / Load Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                prefsManager.saveCustomPreset(config)
                                onConfigChange(
                                    config.copy(
                                        hasCustomPreset = true,
                                        customYOffsetDp = config.yOffsetDp,
                                        customXOffsetDp = config.xOffsetDp,
                                        customCompactWidthDp = config.compactWidthDp,
                                        customCompactHeightDp = config.compactHeightDp,
                                        customCornerRadiusDp = config.cornerRadiusDp
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantGreen)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Save Preset", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Save Custom", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                if (config.hasCustomPreset) {
                                    onConfigChange(
                                        config.copy(
                                            yOffsetDp = config.customYOffsetDp,
                                            xOffsetDp = config.customXOffsetDp,
                                            compactWidthDp = config.customCompactWidthDp,
                                            compactHeightDp = config.customCompactHeightDp,
                                            cornerRadiusDp = config.customCornerRadiusDp
                                        )
                                    )
                                }
                            },
                            enabled = config.hasCustomPreset,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslandAccentPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Load Preset", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (config.hasCustomPreset) "Load Custom" else "No Saved Preset", fontSize = 11.sp)
                        }
                    }

                    if (config.hasCustomPreset) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    prefsManager.clearCustomPreset()
                                    onConfigChange(config.copy(hasCustomPreset = false))
                                }
                            ) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Clear Custom", modifier = Modifier.size(14.dp), tint = TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Clear Saved Custom Preset", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // Manual & Interactive Cutout Calibration Section
            Text(
                text = "Cutout Alignment & Position",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Live Touch Drag Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (config.isCalibrationMode) Color(0x33FF7675) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = "Drag Calibration",
                                tint = if (config.isCalibrationMode) Color(0xFFFF7675) else IslandAccentSecondary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Live Interactive Drag Alignment",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (config.isCalibrationMode) "Drag red overlay handle on screen" else "Touch & drag floating overlay directly around punch-hole",
                                    fontSize = 11.sp,
                                    color = if (config.isCalibrationMode) Color(0xFFFF7675) else TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = config.isCalibrationMode,
                            onCheckedChange = { onConfigChange(config.copy(isCalibrationMode = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-Detect Display Cutout",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Zero-calibration physical camera detection",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = config.isAutoCutoutDetectionEnabled,
                            onCheckedChange = { onConfigChange(config.copy(isAutoCutoutDetectionEnabled = it)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    CalibrationSlider(
                        label = "Vertical Offset (Y-Axis)",
                        value = config.yOffsetDp.toFloat(),
                        range = 0f..150f,
                        onValueChange = { onConfigChange(config.copy(yOffsetDp = it.toInt())) }
                    )

                    CalibrationSlider(
                        label = "Horizontal Offset (X-Axis)",
                        value = config.xOffsetDp.toFloat(),
                        range = -100f..100f,
                        onValueChange = { onConfigChange(config.copy(xOffsetDp = it.toInt())) }
                    )

                    CalibrationSlider(
                        label = "Compact Width",
                        value = config.compactWidthDp.toFloat(),
                        range = 120f..280f,
                        onValueChange = { onConfigChange(config.copy(compactWidthDp = it.toInt())) }
                    )

                    CalibrationSlider(
                        label = "Compact Height",
                        value = config.compactHeightDp.toFloat(),
                        range = 28f..56f,
                        onValueChange = { onConfigChange(config.copy(compactHeightDp = it.toInt())) }
                    )

                    CalibrationSlider(
                        label = "Corner Rounding",
                        value = config.cornerRadiusDp.toFloat(),
                        range = 10f..32f,
                        onValueChange = { onConfigChange(config.copy(cornerRadiusDp = it.toInt())) }
                    )
                }
            }

            // App Notification Mute Filtering Section
            Text(
                text = "App Notification Filtering",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Smart Notification Whitelist Manager",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Control which apps can trigger pills in the Dynamic Island.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search installed apps...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = IslandAccentSecondary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IslandAccentPrimary,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val pm = context.packageManager
                    val installedApps = remember {
                        try {
                            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || pm.getLaunchIntentForPackage(it.packageName) != null }
                                .sortedBy { pm.getApplicationLabel(it).toString() }
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    val filteredApps = remember(installedApps, searchQuery, appFilterTab, mutedPackages) {
                        installedApps.filter { appInfo ->
                            val label = pm.getApplicationLabel(appInfo).toString()
                            val matchesSearch = label.contains(searchQuery, ignoreCase = true) || appInfo.packageName.contains(searchQuery, ignoreCase = true)
                            val isMuted = mutedPackages.contains(appInfo.packageName)
                            val matchesTab = when (appFilterTab) {
                                1 -> !isMuted // Allowed
                                2 -> isMuted  // Blocked
                                else -> true  // All
                            }
                            matchesSearch && matchesTab
                        }
                    }

                    val allowedCount = installedApps.count { !mutedPackages.contains(it.packageName) }
                    val blockedCount = installedApps.count { mutedPackages.contains(it.packageName) }

                    // Filter Tabs & Bulk Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = appFilterTab == 0,
                                onClick = { appFilterTab = 0 },
                                label = { Text("All (${installedApps.size})", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = appFilterTab == 1,
                                onClick = { appFilterTab = 1 },
                                label = { Text("Allowed ($allowedCount)", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = appFilterTab == 2,
                                onClick = { appFilterTab = 2 },
                                label = { Text("Blocked ($blockedCount)", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                prefsManager.setMutedPackages(emptySet())
                                mutedPackages = emptySet()
                            }
                        ) {
                            Text("Allow All", fontSize = 11.sp, color = VibrantGreen)
                        }
                        TextButton(
                            onClick = {
                                val allPkgs = installedApps.map { it.packageName }.toSet()
                                prefsManager.setMutedPackages(allPkgs)
                                mutedPackages = allPkgs
                            }
                        ) {
                            Text("Block All", fontSize = 11.sp, color = VibrantRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (filteredApps.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "No matching apps found" else "No apps in this category",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            filteredApps.forEach { appInfo ->
                                val appLabel = remember(appInfo.packageName) { pm.getApplicationLabel(appInfo).toString() }
                                val isMuted = mutedPackages.contains(appInfo.packageName)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        val iconBitmap = remember(appInfo.packageName) {
                                            try {
                                                pm.getApplicationIcon(appInfo.packageName).toBitmap(56, 56)
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                        if (iconBitmap != null) {
                                            Image(
                                                bitmap = iconBitmap.asImageBitmap(),
                                                contentDescription = appLabel,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Apps,
                                                contentDescription = appLabel,
                                                tint = IslandAccentSecondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = appLabel,
                                                fontSize = 13.sp,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = appInfo.packageName,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = !isMuted,
                                        onCheckedChange = {
                                            prefsManager.toggleMutedPackage(appInfo.packageName)
                                            mutedPackages = prefsManager.getMutedPackages()
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = IslandAccentPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LiveIslandPreviewCard(config: IslandConfig) {
    val themeBrush = when (config.themeStyle) {
        IslandThemeStyle.MIDNIGHT_OLED -> Brush.linearGradient(listOf(Color(0xFF000000), Color(0xFF0D0D12)))
        IslandThemeStyle.CYBERPUNK_NEON -> Brush.linearGradient(listOf(Color(0xFF0F0C20), Color(0xFF2D114C)))
        IslandThemeStyle.SUNSET_GOLD -> Brush.linearGradient(listOf(Color(0xFF1F1000), Color(0xFF381C00)))
        IslandThemeStyle.GLASSMORPHISM -> Brush.linearGradient(listOf(Color(0xCC111118), Color(0xCC22222E)))
    }

    val glowSpotColor = when (config.auraGlowIntensity) {
        AuraGlowIntensity.DISABLED -> Color.Transparent
        AuraGlowIntensity.SUBTLE -> IslandAccentPrimary.copy(alpha = 0.4f)
        AuraGlowIntensity.VIBRANT -> IslandAccentPrimary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Real-Time Aesthetic Render Preview",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Simulated Punch Hole + Island Pill Render
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF0A0A0E), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Punch hole indicator
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )

                // Island Pill Render
                Box(
                    modifier = Modifier
                        .width((config.compactWidthDp * 0.8f).dp)
                        .height((config.compactHeightDp * 0.85f).dp)
                        .shadow(
                            elevation = if (config.auraGlowIntensity == AuraGlowIntensity.DISABLED) 0.dp else 12.dp,
                            shape = RoundedCornerShape(config.cornerRadiusDp.dp),
                            spotColor = glowSpotColor,
                            ambientColor = glowSpotColor
                        )
                        .clip(RoundedCornerShape(config.cornerRadiusDp.dp))
                        .background(themeBrush)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(IslandAccentPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Music",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Starboy • The Weeknd",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Equalizer Visualizer Preview
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.width(2.dp).height(10.dp).background(IslandAccentSecondary))
                            Box(modifier = Modifier.width(2.dp).height(14.dp).background(IslandAccentPrimary))
                            Box(modifier = Modifier.width(2.dp).height(8.dp).background(IslandAccentSecondary))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Size: ${config.compactWidthDp}dp x ${config.compactHeightDp}dp | Rounding: ${config.cornerRadiusDp}dp",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun GestureGuideItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceVariant, shape = RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(IslandAccentPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = IslandAccentSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = description, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = VibrantGreen,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = IslandAccentPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "Grant", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CalibrationSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = TextSecondary)
            Text(text = "${value.toInt()} dp", fontSize = 13.sp, color = IslandAccentSecondary, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = IslandAccentSecondary,
                activeTrackColor = IslandAccentPrimary,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
