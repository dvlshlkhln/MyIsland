package com.myisland.dynamic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.myisland.dynamic.data.IslandConfig
import com.myisland.dynamic.data.IslandMode
import com.myisland.dynamic.data.PreferencesManager
import com.myisland.dynamic.ui.overlay.DynamicIslandView
import com.myisland.dynamic.ui.theme.MyIslandTheme
import com.myisland.dynamic.utils.CameraTorchManager
import kotlinx.coroutines.flow.MutableStateFlow

class IslandOverlayService : LifecycleService(), SavedStateRegistryOwner {

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: ComposeView
    private lateinit var prefsManager: PreferencesManager

    private lateinit var callSessionManager: CallSessionManager
    private lateinit var timerSessionManager: TimerSessionManager
    private lateinit var volumeRingerManager: VolumeRingerManager
    private lateinit var torchManager: CameraTorchManager

    private val screenStateReceiver = ScreenStateReceiver()

    private var currentMode by mutableStateOf(IslandMode.COMPACT)
    private var islandConfig by mutableStateOf(IslandConfig())

    private lateinit var layoutParams: WindowManager.LayoutParams
    private var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

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
        savedStateRegistryController.performRestore(null)

        prefsManager = PreferencesManager(this)
        islandConfig = prefsManager.getConfig()

        callSessionManager = CallSessionManager(this)
        CallStateReceiver.callSessionManager = callSessionManager

        timerSessionManager = TimerSessionManager()
        volumeRingerManager = VolumeRingerManager(this)
        VolumeRingerReceiver.manager = volumeRingerManager
        torchManager = CameraTorchManager(this)

        registerReceivers()
        setupPreferenceListener()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupOverlayView()
    }

    private fun setupPreferenceListener() {
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            islandConfig = prefsManager.getConfig()
            updateWindowLayout(currentMode, ScreenStateReceiver.isScreenOn.value)
        }
        // Fixed: Use exact SharedPreferences name "myisland_preferences"
        val sp = getSharedPreferences("myisland_preferences", Context.MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun registerReceivers() {
        val screenFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        registerReceiver(screenStateReceiver, screenFilter)

        val volumeFilter = IntentFilter().apply {
            addAction("android.media.VOLUME_CHANGED_ACTION")
            addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
        }
        registerReceiver(VolumeRingerReceiver(), volumeFilter)
    }

    private fun updateWindowLayout(mode: IslandMode, isScreenOn: Boolean) {
        if (!::overlayView.isInitialized || !::windowManager.isInitialized || !::layoutParams.isInitialized) return

        val density = resources.displayMetrics.density

        if (!isScreenOn || mode == IslandMode.HIDDEN) {
            layoutParams.width = 1
            layoutParams.height = 1
            layoutParams.y = 0
        } else {
            val (wDp, hDp) = when (mode) {
                IslandMode.COMPACT -> Pair(islandConfig.compactWidthDp, islandConfig.compactHeightDp)
                IslandMode.EXPANDED -> Pair(islandConfig.expandedWidthDp, islandConfig.expandedHeightDp)
                IslandMode.TOAST -> Pair(islandConfig.expandedWidthDp - 20, islandConfig.compactHeightDp + 16)
                else -> Pair(1, 1)
            }

            layoutParams.width = (wDp * density).toInt() + 16
            layoutParams.height = (hDp * density).toInt() + 16
            layoutParams.x = islandConfig.xOffsetDp
            layoutParams.y = (islandConfig.yOffsetDp * density).toInt()
        }

        try {
            windowManager.updateViewLayout(overlayView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupOverlayView() {
        val density = resources.displayMetrics.density
        val initialWidth = ((islandConfig.compactWidthDp) * density).toInt() + 16
        val initialHeight = ((islandConfig.compactHeightDp) * density).toInt() + 16
        val initialY = (islandConfig.yOffsetDp * density).toInt()

        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                if (islandConfig.showOnLockscreen) WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED else 0

        layoutParams = WindowManager.LayoutParams(
            initialWidth,
            initialHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            x = islandConfig.xOffsetDp
            y = initialY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@IslandOverlayService)
            setViewTreeSavedStateRegistryOwner(this@IslandOverlayService)

            setContent {
                MyIslandTheme {
                    val isScreenOn by ScreenStateReceiver.isScreenOn.collectAsState()

                    val mediaState by IslandNotificationListenerService.mediaControllerInstance?.mediaState
                        ?.collectAsState() ?: MutableStateFlow(com.myisland.dynamic.data.MediaState()).collectAsState()

                    val notification by IslandNotificationListenerService.latestNotification.collectAsState()
                    val navigationState by IslandNotificationListenerService.navigationState.collectAsState()
                    val downloadState by IslandNotificationListenerService.downloadState.collectAsState()
                    val recordingState by IslandNotificationListenerService.recordingState.collectAsState()
                    val hotspotState by HotspotStateReceiver.hotspotState.collectAsState()
                    val chargingState by SystemEventReceiver.chargingState.collectAsState()

                    val callState by callSessionManager.callState.collectAsState()
                    val timerState by timerSessionManager.timerState.collectAsState()
                    val bluetoothState by BluetoothEventReceiver.bluetoothState.collectAsState()
                    val volumeRingerState by volumeRingerManager.volumeState.collectAsState()

                    val audioOutputManager = remember { com.myisland.dynamic.utils.AudioOutputManager(this@IslandOverlayService) }

                    LaunchedEffect(currentMode, isScreenOn, islandConfig) {
                        if (islandConfig.isAutoCutoutDetectionEnabled && ::overlayView.isInitialized) {
                            val detected = com.myisland.dynamic.utils.CutoutDetector.detectCutout(overlayView)
                            if (detected.hasCutout) {
                                islandConfig = islandConfig.copy(
                                    yOffsetDp = detected.yOffsetDp,
                                    compactWidthDp = detected.compactWidthDp,
                                    compactHeightDp = detected.compactHeightDp
                                )
                            }
                        }
                        updateWindowLayout(currentMode, isScreenOn)
                    }

                    if (isScreenOn) {
                        this@apply.visibility = View.VISIBLE
                        DynamicIslandView(
                            mode = currentMode,
                            config = islandConfig,
                            mediaState = mediaState,
                            notification = notification,
                            chargingState = chargingState,
                            callState = callState,
                            timerState = timerState,
                            bluetoothState = bluetoothState,
                            volumeRingerState = volumeRingerState,
                            navigationState = navigationState,
                            downloadState = downloadState,
                            recordingState = recordingState,
                            hotspotState = hotspotState,
                            audioOutputManager = audioOutputManager,
                            isCalibrationMode = islandConfig.isCalibrationMode,
                            onPositionDragged = { newX, newY ->
                                islandConfig = islandConfig.copy(xOffsetDp = newX, yOffsetDp = newY)
                                prefsManager.saveConfig(islandConfig)
                            },
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
                            onSeekTo = { posMs ->
                                IslandNotificationListenerService.mediaControllerInstance?.seekTo(posMs)
                            },
                            onDismiss = {
                                currentMode = IslandMode.COMPACT
                            },
                            onSwipeLeftDismiss = {
                                IslandNotificationListenerService.clearLatestNotification()
                                currentMode = IslandMode.COMPACT
                            },
                            onEndCall = {
                                callSessionManager.endCall()
                            },
                            onToggleMuteCall = {
                                callSessionManager.toggleMute()
                            },
                            onAddTimerMinute = {
                                timerSessionManager.addOneMinute()
                            },
                            onToggleTorch = {
                                torchManager.toggleTorch()
                            },
                            onToggleAudioOutput = {
                                try {
                                    val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            onMuteActiveApp = {
                                IslandNotificationListenerService.clearLatestNotification()
                                currentMode = IslandMode.COMPACT
                            }
                        )
                    } else {
                        this@apply.visibility = View.GONE
                    }
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
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        preferenceChangeListener?.let { listener ->
            val sp = getSharedPreferences("myisland_preferences", Context.MODE_PRIVATE)
            sp.unregisterOnSharedPreferenceChangeListener(listener)
        }

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
        .setContentText("Dynamic Island active with Lockscreen AOD & Live Maps Navigation")
        .setSmallIcon(android.R.drawable.ic_menu_compass)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()
}
