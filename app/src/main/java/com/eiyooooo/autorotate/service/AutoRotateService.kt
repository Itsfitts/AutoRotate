package com.eiyooooo.autorotate.service

import android.annotation.SuppressLint
import android.util.Log
import com.eiyooooo.autorotate.data.ScreenConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@SuppressLint("LogNotTimber")
class AutoRotateService : IAutoRotateService.Stub() {

    private val configs = mutableListOf<ScreenConfig>()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    override fun updateConfigs(newConfigs: List<ScreenConfig>) {
        configs.clear()
        configs.addAll(newConfigs)
        Log.d("AutoRotateService", "Received ${configs.size} configs")
        job?.cancel()
        if (configs.isNotEmpty()) {
            job = scope.launch {
                var message = 0
                while (isActive) {
                    delay(5000)
                    message++
                    Log.d("AutoRotateService", "Message: $message")
                    for (config in configs) {
                        Log.d(
                            "AutoRotateService",
                            "Received config: orientation: ${config.orientation}, address: ${config.displayAddress}, displayName: ${config.displayName}"
                        )
                    }
                }
            }
        }
    }

    override fun destroy() {
        exitProcess(0)
    }
}
