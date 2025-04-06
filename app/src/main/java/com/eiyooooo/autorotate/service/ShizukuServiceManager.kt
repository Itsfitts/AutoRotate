package com.eiyooooo.autorotate.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.eiyooooo.autorotate.BuildConfig
import com.eiyooooo.autorotate.data.ScreenConfig
import com.eiyooooo.autorotate.data.ShizukuStatus
import com.eiyooooo.autorotate.entity.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku
import timber.log.Timber

class ShizukuServiceManager {

    private val _shizukuStatus = MutableStateFlow(ShizukuStatus.SHIZUKU_NOT_RUNNING)
    val shizukuStatus: StateFlow<ShizukuStatus> = _shizukuStatus

    private val _serviceEnabled = MutableStateFlow(Preferences.serviceEnabled)
    val serviceEnabled: StateFlow<Boolean> = _serviceEnabled

    private var tempConfigs: List<ScreenConfig>? = null
    private var service: IAutoRotateService? = null

    private val bindListener = Shizuku.OnBinderReceivedListener {
        checkShizukuPermission()
        if (_shizukuStatus.value == ShizukuStatus.HAVE_PERMISSION) {
            bindService()
        }
    }

    private val bindDeadListener = Shizuku.OnBinderDeadListener {
        service = null
        _shizukuStatus.value = ShizukuStatus.SHIZUKU_NOT_RUNNING
    }

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                _shizukuStatus.value = ShizukuStatus.HAVE_PERMISSION
                bindService()
            } else {
                _shizukuStatus.value = ShizukuStatus.NO_PERMISSION
            }
        }
    }

    private val userServiceArgs: Shizuku.UserServiceArgs =
        Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, AutoRotateService::class.java.getName()))
            .daemon(true)
            .processNameSuffix("service")
            .debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = IAutoRotateService.Stub.asInterface(binder)
            tempConfigs?.let {
                Timber.d("Update configs: ${it.size} configs, after bind")
                updateConfigs(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    init {
        Shizuku.addBinderReceivedListenerSticky(bindListener)
        Shizuku.addBinderDeadListener(bindDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionListener)

        if (Shizuku.pingBinder()) {
            checkShizukuPermission()
            if (_shizukuStatus.value == ShizukuStatus.HAVE_PERMISSION) {
                bindService()
            }
        }
    }

    fun destroy() {
        Shizuku.removeBinderReceivedListener(bindListener)
        Shizuku.removeBinderDeadListener(bindDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionListener)
    }

    fun setServiceEnabled(enabled: Boolean) {
        _serviceEnabled.value = enabled
        Preferences.serviceEnabled = enabled

        if (enabled) {
            if (_shizukuStatus.value == ShizukuStatus.HAVE_PERMISSION) {
                bindService()
            }
        } else {
            unbindService()
        }
    }

    fun requestPermission() {
        if (!Shizuku.pingBinder()) {
            _shizukuStatus.value = ShizukuStatus.SHIZUKU_NOT_RUNNING
            return
        }

        try {
            Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            Timber.e(e, "Request permission failed")
            _shizukuStatus.value = ShizukuStatus.VERSION_NOT_SUPPORT
        }
    }

    fun updateConfigs(configs: List<ScreenConfig>) {
        tempConfigs = configs
        if (_shizukuStatus.value != ShizukuStatus.HAVE_PERMISSION) {
            return
        }
        service?.let {
            try {
                it.updateConfigs(configs)
                tempConfigs = null
                Timber.d("Update configs: ${configs.size} configs")
                return
            } catch (e: Exception) {
                Timber.e(e, "Update configs failed")
            }
        }
        bindService()
    }

    fun checkShizukuPermission() {
        if (!Shizuku.pingBinder()) {
            _shizukuStatus.value = ShizukuStatus.SHIZUKU_NOT_RUNNING
            return
        }

        if (Shizuku.isPreV11()) {
            _shizukuStatus.value = ShizukuStatus.VERSION_NOT_SUPPORT
            return
        }

        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                _shizukuStatus.value = ShizukuStatus.HAVE_PERMISSION
            } else {
                _shizukuStatus.value = ShizukuStatus.NO_PERMISSION
            }
        } catch (e: Exception) {
            Timber.e(e, "Check permission failed")
            _shizukuStatus.value = ShizukuStatus.VERSION_NOT_SUPPORT
        }
    }

    private fun bindService() {
        if (!_serviceEnabled.value) return

        try {
            Shizuku.unbindUserService(userServiceArgs, serviceConnection, true)
            Shizuku.bindUserService(userServiceArgs, serviceConnection)
            Timber.d("Bind service successfully")
        } catch (e: Exception) {
            Timber.e(e, "Bind service failed")
            service = null
        }
    }

    private fun unbindService() {
        try {
            Shizuku.unbindUserService(userServiceArgs, serviceConnection, true)
            service = null
            Timber.d("Unbind service successfully")
        } catch (e: Exception) {
            Timber.e(e, "Unbind service failed")
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
