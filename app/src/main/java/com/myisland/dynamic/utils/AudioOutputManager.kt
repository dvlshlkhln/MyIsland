package com.myisland.dynamic.utils

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.myisland.dynamic.data.AudioDeviceType
import com.myisland.dynamic.data.AudioOutputDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioOutputManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _availableDevices = MutableStateFlow<List<AudioOutputDevice>>(emptyList())
    val availableDevices: StateFlow<List<AudioOutputDevice>> = _availableDevices.asStateFlow()

    init {
        refreshDevices()
    }

    fun refreshDevices() {
        val deviceList = mutableListOf<AudioOutputDevice>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val activeCommunicationDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                audioManager.communicationDevice
            } else null

            for (device in outputs) {
                val (name, type) = when (device.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> Pair("Phone Speaker", AudioDeviceType.SPEAKER)
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> Pair(device.productName.toString().ifBlank { "Bluetooth Headphones" }, AudioDeviceType.BLUETOOTH)
                    AudioDeviceInfo.TYPE_WIRED_HEADSET,
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_USB_HEADSET -> Pair("Wired Headphones", AudioDeviceType.HEADPHONES)
                    else -> Pair("External Audio Device", AudioDeviceType.SPEAKER)
                }

                val isActive = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && activeCommunicationDevice != null) {
                    activeCommunicationDevice.id == device.id
                } else if (type == AudioDeviceType.BLUETOOTH) {
                    audioManager.isBluetoothA2dpOn
                } else {
                    audioManager.isSpeakerphoneOn
                }

                deviceList.add(
                    AudioOutputDevice(
                        id = device.id.toString(),
                        name = name,
                        type = type,
                        isActive = isActive
                    )
                )
            }
        } else {
            deviceList.add(AudioOutputDevice("1", "Phone Speaker", AudioDeviceType.SPEAKER, !audioManager.isBluetoothA2dpOn))
            if (audioManager.isBluetoothA2dpOn) {
                deviceList.add(AudioOutputDevice("2", "Bluetooth Audio", AudioDeviceType.BLUETOOTH, true))
            }
        }

        _availableDevices.value = deviceList.ifEmpty {
            listOf(AudioOutputDevice("default", "Phone Speaker", AudioDeviceType.SPEAKER, true))
        }
    }

    fun selectAudioDevice(device: AudioOutputDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val target = outputs.firstOrNull { it.id.toString() == device.id }
            if (target != null) {
                audioManager.setCommunicationDevice(target)
            }
        } else {
            if (device.type == AudioDeviceType.SPEAKER) {
                audioManager.isSpeakerphoneOn = true
            } else if (device.type == AudioDeviceType.BLUETOOTH) {
                audioManager.isBluetoothA2dpOn = true
            }
        }
        refreshDevices()
    }
}
