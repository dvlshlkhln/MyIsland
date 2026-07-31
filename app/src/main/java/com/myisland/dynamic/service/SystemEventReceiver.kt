package com.myisland.dynamic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.BatteryManager
import com.myisland.dynamic.data.ChargingState
import com.myisland.dynamic.data.RingerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SystemEventReceiver : BroadcastReceiver() {

    companion object {
        private val _chargingState = MutableStateFlow(ChargingState())
        val chargingState: StateFlow<ChargingState> = _chargingState.asStateFlow()

        private val _ringerState = MutableStateFlow(RingerState())
        val ringerState: StateFlow<RingerState> = _ringerState.asStateFlow()
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                val batteryLevel = getBatteryLevel(context)
                _chargingState.value = ChargingState(isCharging = true, batteryLevel = batteryLevel)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                val batteryLevel = getBatteryLevel(context)
                _chargingState.value = ChargingState(isCharging = false, batteryLevel = batteryLevel)
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (level >= 0 && scale > 0) (level * 100 / scale.toFloat()).toInt() else 100
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

                _chargingState.value = ChargingState(isCharging = isCharging, batteryLevel = pct)
            }
            AudioManager.RINGER_MODE_CHANGED_ACTION -> {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
                val modeStr = when (ringerMode) {
                    AudioManager.RINGER_MODE_SILENT -> "Silent"
                    AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
                    else -> "Ring"
                }
                _ringerState.value = RingerState(modeName = modeStr)
            }
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale.toFloat()).toInt() else 100
    }
}
