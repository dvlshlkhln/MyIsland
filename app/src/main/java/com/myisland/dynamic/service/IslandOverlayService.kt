package com.myisland.dynamic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.myisland.dynamic.data.IslandConfig
import com.myisland.dynamic.data.IslandMode
import com.myisland.dynamic.data.PreferencesManager
import com.myisland.dynamic.ui.overlay.DynamicIslandView
import com.myisland.dynamic.ui.theme.MyIslandTheme
import kotlinx.coroutines.flow.MutableStateFlow

class IslandOverlayService : LifecycleService() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: ComposeView
    private lateinit var prefsManager: PreferencesManager

    private var currentMode by mutableStateOf(IslandMode.COMPACT)
    private var islandConfig by mutableStateOf(IslandConfig())

    companion object {
        const val CHANNEL_ID = "myisland_service_channel"
        const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, IslandOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, IslandOverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
        islandConfig = prefsManager.getConfig()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupOverlayView()
    }

    private fun setupOverlayView() {
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = islandConfig.yOffsetDp
        }

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@IslandOverlayService)
            setViewTreeSavedStateRegistryOwner(this@IslandOverlayService)

            setContent {
                MyIslandTheme {
                    val mediaState by IslandNotificationListenerService.mediaControllerInstance?.mediaState
                        ?.collectAsState() ?: MutableStateFlow(com.myisland.dynamic.data.MediaState()).collectAsState()

                    val notification by IslandNotificationListenerService.latestNotification.collectAsState()
                    val chargingState by SystemEventReceiver.chargingState.collectAsState()

                    DynamicIslandView(
                        mode = currentMode,
                        config = islandConfig,
                        mediaState = mediaState,
                        notification = notification,
                        chargingState = chargingState,
                        onToggleExpand = {
                            currentMode = if (currentMode == IslandMode.EXPANDED) IslandMode.COMPACT else IslandMode.EXPANDED
                        },
                        onPlayPauseToggle = {
                            IslandNotificationListenerService.mediaControllerInstance?.togglePlayPause()
                        },
                        onSkipNext = {
                            IslandNotificationListenerService.mediaControllerInstance?.skipToNext()
                        },
                        onSkipPrevious = {
                            IslandNotificationListenerService.mediaControllerInstance?.skipToPrevious()
                        },
                        onDismiss = {
                            currentMode = IslandMode.COMPACT
                        }
                    )
                }
            }
        }

        try {
            windowManager.addView(overlayView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MyIsland Overlay Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the Dynamic Island overlay active"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("MyIsland is Running")
        .setContentText("Dynamic Island active on Motorola Edge 60 Pro")
        .setSmallIcon(android.R.drawable.ic_menu_compass)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()
}
