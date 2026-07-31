package com.myisland.dynamic.ui.settings

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.myisland.dynamic.data.DevicePreset
import com.myisland.dynamic.data.DevicePresets
import com.myisland.dynamic.data.IslandConfig
import com.myisland.dynamic.data.PreferencesManager
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
                    Column {
                        Text(
                            text = "Dynamic Island Service",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isServiceRunning) "Service is active & floating" else "Service is turned off",
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
                    Text(
                        text = "1-Tap Preset Calibration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
                }
            }

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

            // Manual Cutout Calibration Section
            Text(
                text = "Fine-Tune Position & Size",
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
                        text = "Custom Cutout Alignment",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
                        text = "Notification Whitelist / Blacklist",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toggle off any app to block its notifications from appearing in the Dynamic Island.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val pm = context.packageManager
                    val installedApps = remember {
                        try {
                            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                                .take(12)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    installedApps.forEach { appInfo ->
                        val appLabel = pm.getApplicationLabel(appInfo).toString()
                        val isMuted = mutedPackages.contains(appInfo.packageName)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val iconBitmap = remember(appInfo.packageName) {
                                    try {
                                        pm.getApplicationIcon(appInfo.packageName).toBitmap(64, 64)
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
                                Text(
                                    text = appLabel,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
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

            Spacer(modifier = Modifier.height(24.dp))
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
