package com.myisland.dynamic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VolumeRingerState(
    val volumePercent: Int = 0,
    val isMuted: Boolean = false,
    val modeName: String = "Ring", // Silent, Vibrate, Ring
    val isVolumeEvent: Boolean = false
)

class VolumeRingerManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val _volumeState = MutableStateFlow(VolumeRingerState())
    val volumeState: StateFlow<VolumeRingerState> = _volumeState.asStateFlow()

    fun updateVolume(streamType: Int) {
        audioManager?.let { am ->
            val current = am.getStreamVolume(streamType)
            val max = am.getStreamMaxVolume(streamType)
            val pct = if (max > 0) (current * 100 / max.toFloat()).toInt() else 0
            val isMuted = current == 0

            _volumeState.value = VolumeRingerState(
                volumePercent = pct,
                isMuted = isMuted,
                modeName = getRingerModeName(am.ringerMode),
                isVolumeEvent = true
            )
        }
    }

    fun updateRingerMode(ringerMode: Int) {
        _volumeState.value = _volumeState.value.copy(
            modeName = getRingerModeName(ringerMode),
            isVolumeEvent = false
        )
    }

    private fun getRingerModeName(mode: Int): String {
        return when (mode) {
            AudioManager.RINGER_MODE_SILENT -> "Silent"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            else -> "Ring"
        }
    }
}

class VolumeRingerReceiver : BroadcastReceiver() {
    companion object {
        var manager: VolumeRingerManager? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.media.VOLUME_CHANGED_ACTION" -> {
                val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", AudioManager.STREAM_MUSIC)
                manager?.updateVolume(streamType)
            }
            AudioManager.RINGER_MODE_CHANGED_ACTION -> {
                val mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)
                manager?.updateRingerMode(mode)
            }
        }
    }
}
