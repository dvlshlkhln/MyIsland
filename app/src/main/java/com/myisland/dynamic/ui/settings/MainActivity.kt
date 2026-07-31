package com.myisland.dynamic.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.myisland.dynamic.data.IslandConfig
import com.myisland.dynamic.data.PreferencesManager
import com.myisland.dynamic.service.IslandOverlayService
import com.myisland.dynamic.ui.theme.MyIslandTheme
import com.myisland.dynamic.utils.PermissionUtils

class MainActivity : ComponentActivity() {

    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PreferencesManager(this)

        setContent {
            MyIslandTheme {
                var config by remember { mutableStateOf(prefsManager.getConfig()) }
                var isServiceRunning by remember { mutableStateOf(prefsManager.isServiceEnabled()) }
                var hasOverlayPerm by remember { mutableStateOf(PermissionUtils.hasOverlayPermission(this)) }
                var hasNotifPerm by remember { mutableStateOf(PermissionUtils.hasNotificationListenerPermission(this)) }

                // Refresh permissions on resume state
                LaunchedEffect(Unit) {
                    hasOverlayPerm = PermissionUtils.hasOverlayPermission(this@MainActivity)
                    hasNotifPerm = PermissionUtils.hasNotificationListenerPermission(this@MainActivity)
                }

                SettingsDashboardScreen(
                    config = config,
                    isServiceRunning = isServiceRunning,
                    hasOverlayPerm = hasOverlayPerm,
                    hasNotifPerm = hasNotifPerm,
                    onToggleService = { enabled ->
                        isServiceRunning = enabled
                        prefsManager.setServiceEnabled(enabled)
                        if (enabled) {
                            if (PermissionUtils.hasOverlayPermission(this@MainActivity)) {
                                IslandOverlayService.startService(this@MainActivity)
                            } else {
                                PermissionUtils.requestOverlayPermission(this@MainActivity)
                            }
                        } else {
                            IslandOverlayService.stopService(this@MainActivity)
                        }
                    },
                    onConfigChange = { newConfig ->
                        config = newConfig
                        prefsManager.saveConfig(newConfig)
                    },
                    onRequestOverlayPerm = {
                        PermissionUtils.requestOverlayPermission(this@MainActivity)
                    },
                    onRequestNotifPerm = {
                        PermissionUtils.requestNotificationListenerPermission(this@MainActivity)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::prefsManager.isInitialized) {
            val isEnabled = prefsManager.isServiceEnabled()
            val hasOverlay = PermissionUtils.hasOverlayPermission(this)
            if (isEnabled && hasOverlay) {
                IslandOverlayService.startService(this)
            }
        }
    }
}
