package com.myisland.dynamic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScreenStateReceiver : BroadcastReceiver() {

    companion object {
        private val _isScreenOn = MutableStateFlow(true)
        val isScreenOn: StateFlow<Boolean> = _isScreenOn.asStateFlow()

        private val _isPowerSaveMode = MutableStateFlow(false)
        val isPowerSaveMode: StateFlow<Boolean> = _isPowerSaveMode.asStateFlow()
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                _isScreenOn.value = false
            }
            Intent.ACTION_SCREEN_ON -> {
                _isScreenOn.value = true
                checkPowerSaveMode(context)
            }
            PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                checkPowerSaveMode(context)
            }
        }
    }

    private fun checkPowerSaveMode(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        _isPowerSaveMode.value = powerManager?.isPowerSaveMode == true
    }
}
