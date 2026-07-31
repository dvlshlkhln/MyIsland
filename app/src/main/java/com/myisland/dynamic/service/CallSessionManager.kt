package com.myisland.dynamic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.myisland.dynamic.data.CallState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallSessionManager(private val context: Context) {

    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun updateCallState(state: Int, incomingNumber: String? = null) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                stopTimer()
                _callState.value = CallState(
                    callerName = incomingNumber ?: "Incoming Call",
                    callerNumber = incomingNumber ?: "",
                    isRinging = true,
                    isActiveCall = false,
                    callDurationSeconds = 0
                )
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                stopTimer()
                _callState.value = CallState(
                    callerName = incomingNumber ?: "Active Call",
                    callerNumber = incomingNumber ?: "",
                    isRinging = false,
                    isActiveCall = true,
                    callDurationSeconds = 0
                )
                startTimer()
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                stopTimer()
                _callState.value = CallState()
            }
        }
    }

    private fun startTimer() {
        timerJob = scope.launch {
            var seconds = 0
            while (isActive) {
                delay(1000)
                seconds++
                _callState.value = _callState.value.copy(callDurationSeconds = seconds)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun toggleMute() {
        _callState.value = _callState.value.copy(isMuted = !_callState.value.isMuted)
    }

    fun endCall() {
        stopTimer()
        _callState.value = CallState()
    }
}

class CallStateReceiver : BroadcastReceiver() {
    companion object {
        var callSessionManager: CallSessionManager? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            val state = when (stateStr) {
                TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
                TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
                else -> TelephonyManager.CALL_STATE_IDLE
            }

            callSessionManager?.updateCallState(state, incomingNumber)
        }
    }
}
