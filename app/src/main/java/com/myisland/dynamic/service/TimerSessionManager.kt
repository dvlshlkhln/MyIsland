package com.myisland.dynamic.service

import com.myisland.dynamic.data.TimerState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerSessionManager {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun startTimer(title: String = "Timer", durationSeconds: Int) {
        timerJob?.cancel()
        _timerState.value = TimerState(
            title = title,
            remainingSeconds = durationSeconds,
            totalSeconds = durationSeconds,
            isRunning = true
        )

        timerJob = scope.launch {
            var current = durationSeconds
            while (current > 0 && isActive) {
                delay(1000)
                current--
                _timerState.value = _timerState.value.copy(remainingSeconds = current)
            }
            _timerState.value = _timerState.value.copy(isRunning = false)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun addOneMinute() {
        val updated = _timerState.value.remainingSeconds + 60
        val total = _timerState.value.totalSeconds + 60
        _timerState.value = _timerState.value.copy(
            remainingSeconds = updated,
            totalSeconds = total
        )
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }
}
