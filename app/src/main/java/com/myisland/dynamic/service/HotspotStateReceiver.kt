package com.myisland.dynamic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import com.myisland.dynamic.data.HotspotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HotspotStateReceiver : BroadcastReceiver() {

    companion object {
        private val _hotspotState = MutableStateFlow(HotspotState())
        val hotspotState: StateFlow<HotspotState> = _hotspotState.asStateFlow()

        private var lastRxBytes = 0L
        private var lastTxBytes = 0L
        private var lastTimestamp = 0L

        fun updateHotspotState(isActive: Boolean, clientCount: Int = 1) {
            val currentRx = TrafficStats.getTotalRxBytes()
            val currentTx = TrafficStats.getTotalTxBytes()
            val now = System.currentTimeMillis()

            val speedKbps = if (lastTimestamp > 0 && now > lastTimestamp) {
                val timeDiffSec = (now - lastTimestamp) / 1000f
                if (timeDiffSec > 0) {
                    val bytesDiff = (currentRx - lastRxBytes) + (currentTx - lastTxBytes)
                    ((bytesDiff / 1024f) / timeDiffSec).toLong().coerceAtLeast(0L)
                } else 0L
            } else 0L

            lastRxBytes = currentRx
            lastTxBytes = currentTx
            lastTimestamp = now

            _hotspotState.value = HotspotState(
                isActive = isActive,
                clientCount = if (isActive) clientCount.coerceAtLeast(1) else 0,
                speedKbps = speedKbps
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action ?: return

        if (action == "android.net.wifi.WIFI_AP_STATE_CHANGED") {
            val state = intent.getIntExtra("wifi_state", 0)
            // AP state active is 13 in Android WifiManager
            val isActive = state == 13
            updateHotspotState(isActive = isActive, clientCount = 1)
        }
    }
}
