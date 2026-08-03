package com.myisland.dynamic.service

import android.app.Notification
import android.content.ComponentName
import android.graphics.drawable.BitmapDrawable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.myisland.dynamic.data.NavigationDirection
import com.myisland.dynamic.data.NavigationState
import com.myisland.dynamic.data.NotificationItem
import com.myisland.dynamic.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.myisland.dynamic.data.DownloadState
import com.myisland.dynamic.data.RecordingState

class IslandNotificationListenerService : NotificationListenerService() {

    private lateinit var prefsManager: PreferencesManager

    companion object {
        private val _latestNotification = MutableStateFlow<NotificationItem?>(null)
        val latestNotification: StateFlow<NotificationItem?> = _latestNotification.asStateFlow()

        private val _navigationState = MutableStateFlow(NavigationState())
        val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

        private val _downloadState = MutableStateFlow(DownloadState())
        val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

        private val _recordingState = MutableStateFlow(RecordingState())
        val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

        var mediaControllerInstance: MediaSessionController? = null
            private set

        fun clearLatestNotification() {
            _latestNotification.value = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        val componentName = ComponentName(this, IslandNotificationListenerService::class.java)
        mediaControllerInstance = MediaSessionController(this).also {
            it.init(componentName)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkg = sbn.packageName
        if (pkg == packageName || pkg == "android" || pkg == "com.android.systemui" || pkg == "com.google.android.systemui") return

        // Check if package is muted by user
        if (::prefsManager.isInitialized && prefsManager.getMutedPackages().contains(pkg)) {
            return
        }

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val combinedText = "$title $text".lowercase()

        // Filter out system overlay warnings (e.g. "displaying over other apps", "drawing over other apps", "running in background")
        if (combinedText.contains("displaying over other apps") ||
            combinedText.contains("drawing over other apps") ||
            combinedText.contains("is displaying over") ||
            combinedText.contains("running in the background") ||
            combinedText.contains("overlay active")) {
            return
        }

        // 1. Handle Screen Recording & Voice Recording Notifications
        if (pkg.contains("screenrecord") || pkg.contains("recorder") || title.contains("recording", ignoreCase = true) || text.contains("recording", ignoreCase = true)) {
            val isPaused = title.contains("pause", ignoreCase = true) || text.contains("pause", ignoreCase = true)
            val recType = if (pkg.contains("screen")) "Screen Recording" else "Voice Recording"
            _recordingState.value = RecordingState(
                isRecording = true,
                type = recType,
                durationSeconds = 12,
                isPaused = isPaused
            )
            return
        }

        // 2. Handle Live Downloads Notifications
        val progressMax = extras.getInt(Notification.EXTRA_PROGRESS_MAX, 0)
        val progressCurrent = extras.getInt(Notification.EXTRA_PROGRESS, 0)
        if (progressMax > 0 || pkg.contains("android.providers.downloads") || title.contains("download", ignoreCase = true)) {
            val percent = if (progressMax > 0) ((progressCurrent.toFloat() / progressMax.toFloat()) * 100).toInt() else 45
            _downloadState.value = DownloadState(
                isDownloading = true,
                fileName = title.ifBlank { "Downloading File..." },
                progressPercent = percent.coerceIn(0, 100),
                bytesPerSec = 2400000L,
                appName = if (pkg.contains("chrome")) "Chrome" else "Download Manager"
            )
            return
        }

        // 3. Handle Navigation Maps Apps
        if (pkg.contains("apps.maps") || pkg.contains("waze")) {
            if (combinedText.contains("head") || combinedText.contains("turn") || combinedText.contains("continue") || combinedText.contains("destination") || combinedText.contains("exit") || combinedText.contains("onto")) {
                val direction = parseNavigationDirection("$title $text")
                _navigationState.value = NavigationState(
                    isNavigating = true,
                    direction = direction,
                    distanceText = extractDistance("$title $text"),
                    streetName = title.ifBlank { text },
                    appName = if (pkg.contains("waze")) "Waze" else "Google Maps"
                )
                return
            }
            if (sbn.isOngoing) return
        }

        if (sbn.isOngoing) return
        if (title.isBlank() && text.isBlank()) return

        val pm = packageManager
        val appName = try {
            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
        } catch (e: Exception) {
            pkg
        }

        val appIconDrawable = try {
            pm.getApplicationIcon(pkg)
        } catch (e: Exception) {
            null
        }

        val iconBitmap = (appIconDrawable as? BitmapDrawable)?.bitmap

        _latestNotification.value = NotificationItem(
            id = sbn.key,
            packageName = pkg,
            appName = appName,
            title = title,
            message = text,
            icon = iconBitmap,
            timestamp = sbn.postTime
        )
    }

    private fun parseNavigationDirection(text: String): NavigationDirection {
        val lower = text.lowercase()
        return when {
            lower.contains("left") -> NavigationDirection.TURN_LEFT
            lower.contains("right") -> NavigationDirection.TURN_RIGHT
            lower.contains("u-turn") -> NavigationDirection.U_TURN
            lower.contains("arrive") || lower.contains("destination") -> NavigationDirection.ARRIVE
            else -> NavigationDirection.STRAIGHT
        }
    }

    private fun extractDistance(text: String): String {
        val regex = Regex("""\d+\s*(m|km|ft|mi)""", RegexOption.IGNORE_CASE)
        return regex.find(text)?.value ?: "Ahead"
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null) {
            if (_latestNotification.value?.id == sbn.key) {
                _latestNotification.value = null
            }
            if (sbn.packageName.contains("apps.maps") || sbn.packageName.contains("waze")) {
                _navigationState.value = NavigationState(isNavigating = false)
            }
            if (sbn.packageName.contains("screenrecord") || sbn.packageName.contains("recorder")) {
                _recordingState.value = RecordingState(isRecording = false)
            }
            if (sbn.packageName.contains("android.providers.downloads") || sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS_MAX, 0) > 0) {
                _downloadState.value = DownloadState(isDownloading = false)
            }
        }
    }
}
