package com.eiyooooo.autorotate.service

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import com.eiyooooo.autorotate.data.ScreenConfig
import com.eiyooooo.autorotate.wrapper.DisplayManager
import com.eiyooooo.autorotate.wrapper.DisplayMonitor
import com.eiyooooo.autorotate.wrapper.WindowManager
import kotlin.system.exitProcess

@SuppressLint("LogNotTimber")
class AutoRotateService : IAutoRotateService.Stub() {

    private val configs = mutableListOf<ScreenConfig>()

    private lateinit var displayMonitor: DisplayMonitor

    override fun updateConfigs(newConfigs: List<ScreenConfig>) {
        configs.clear()
        configs.addAll(newConfigs)
        Log.d("AutoRotateService", "Received ${configs.size} configs")

        updateAllDisplayRotation()

        if (!::displayMonitor.isInitialized) {
            displayMonitor = DisplayMonitor().apply {
                start { eventDisplayId ->
                    Log.d("AutoRotateService", "Received display event at displayId: $eventDisplayId")
                    updateAllDisplayRotation()
                }
            }
        }
    }

    private fun updateAllDisplayRotation() {
        val allDisplayInfo = DisplayManager.getInstance().getAllDisplayInfo()
        for (displayInfo in allDisplayInfo) {
            displayInfo.address?.let { displayAddress ->
                configs.find { it.displayAddress == displayAddress.toString() }?.let { config ->
                    Log.d("AutoRotateService", "Updating display rotation for displayId: ${displayInfo.displayId}")
                    if (config.orientation == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) {
                        WindowManager.getInstance().thawRotation(displayInfo.displayId)
                    } else if (config.orientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                        WindowManager.getInstance().freezeRotation(displayInfo.displayId, displayInfo.rotation)
                    }
                }
            } ?: run {
                Log.d("AutoRotateService", "No address found for displayId: ${displayInfo.displayId}")
            }
        }
    }

    override fun destroy() {
        if (::displayMonitor.isInitialized) {
            displayMonitor.stopAndRelease()
        }
        exitProcess(0)
    }
}
