package com.myisland.dynamic.service

import android.app.Notification
import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.myisland.dynamic.data.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IslandNotificationListenerService : NotificationListenerService() {

    companion object {
        private val _latestNotification = MutableStateFlow<NotificationItem?>(null)
        val latestNotification: StateFlow<NotificationItem?> = _latestNotification.asStateFlow()

        var mediaControllerInstance: MediaSessionController? = null
            private set
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
        if (sbn == null || sbn.isOngoing) return

        val pkg = sbn.packageName
        if (pkg == packageName) return // Ignore self notifications

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

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

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null && _latestNotification.value?.id == sbn.key) {
            _latestNotification.value = null
        }
    }
}
