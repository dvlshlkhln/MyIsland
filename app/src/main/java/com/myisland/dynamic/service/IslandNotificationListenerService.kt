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

class IslandNotificationListenerService : NotificationListenerService() {

    private lateinit var prefsManager: PreferencesManager

    companion object {
        private val _latestNotification = MutableStateFlow<NotificationItem?>(null)
        val latestNotification: StateFlow<NotificationItem?> = _latestNotification.asStateFlow()

        private val _navigationState = MutableStateFlow(NavigationState())
        val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

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
        if (pkg == packageName) return

        // Check if package is muted by user
        if (::prefsManager.isInitialized && prefsManager.getMutedPackages().contains(pkg)) {
            return
        }

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        // Handle Navigation Maps Apps
        if (pkg.contains("apps.maps") || pkg.contains("waze") || title.contains("turn", ignoreCase = true) || text.contains("turn", ignoreCase = true)) {
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
        }
    }
}
