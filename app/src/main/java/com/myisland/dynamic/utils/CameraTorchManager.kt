package com.myisland.dynamic.utils

import android.content.Context
import android.hardware.camera2.CameraManager

class CameraTorchManager(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var isTorchOn = false

    fun toggleTorch(): Boolean {
        try {
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return false
            isTorchOn = !isTorchOn
            cameraManager?.setTorchMode(cameraId, isTorchOn)
            return isTorchOn
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun isTorchActive(): Boolean = isTorchOn
}
