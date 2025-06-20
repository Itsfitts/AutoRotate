package com.eiyooooo.autorotate

import android.app.Application
import android.content.Context
import android.os.Build
import com.eiyooooo.autorotate.entity.Preferences
import com.eiyooooo.autorotate.service.ShizukuServiceManager
import com.eiyooooo.autorotate.util.FLog
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber
import java.util.Date

lateinit var application: MyApplication private set

class MyApplication : Application() {

    companion object {
        lateinit var appStartTime: Date
            private set
    }

    lateinit var shizukuServiceManager: ShizukuServiceManager
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("L")
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        appStartTime = Date()

        Preferences.init(this)

        FLog.init(this)
        if (Preferences.enableLog) FLog.startFLog()
        Timber.i("App started at: $appStartTime")

        shizukuServiceManager = ShizukuServiceManager(this)
    }
}
