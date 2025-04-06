package com.eiyooooo.autorotate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eiyooooo.autorotate.data.ScreenConfigRepository
import com.eiyooooo.autorotate.data.ShizukuStatus
import com.eiyooooo.autorotate.service.ShizukuServiceManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoRotateViewModel(application: Application) : AndroidViewModel(application) {

    private val shizukuServiceManager = ShizukuServiceManager()
    private val repository = ScreenConfigRepository(application)

    val shizukuStatus: StateFlow<ShizukuStatus> = shizukuServiceManager.shizukuStatus
    val serviceEnabled: StateFlow<Boolean> = shizukuServiceManager.serviceEnabled

    init {
        viewModelScope.launch {
            repository.configs.collect { newConfigs ->
                shizukuServiceManager.updateConfigs(newConfigs)
            }
        }
    }

    fun requestShizukuPermission() {
        shizukuServiceManager.requestPermission()
    }

    fun checkShizukuPermission() {
        shizukuServiceManager.checkShizukuPermission()
    }

    fun setServiceEnabled(enabled: Boolean) {
        shizukuServiceManager.setServiceEnabled(enabled)
    }

    override fun onCleared() {
        super.onCleared()
        shizukuServiceManager.destroy()
    }
}
