package com.myisland.dynamic.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myisland.dynamic.data.BluetoothDeviceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothEventReceiver : BroadcastReceiver() {

    companion object {
        private val _bluetoothState = MutableStateFlow(BluetoothDeviceState())
        val bluetoothState: StateFlow<BluetoothDeviceState> = _bluetoothState.asStateFlow()
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name ?: "Bluetooth Headphones"
                _bluetoothState.value = BluetoothDeviceState(
                    deviceName = deviceName,
                    isConnected = true,
                    batteryLevel = -1
                )
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                _bluetoothState.value = BluetoothDeviceState(
                    deviceName = "",
                    isConnected = false,
                    batteryLevel = -1
                )
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state == BluetoothAdapter.STATE_OFF) {
                    _bluetoothState.value = BluetoothDeviceState()
                }
            }
        }
    }
}
