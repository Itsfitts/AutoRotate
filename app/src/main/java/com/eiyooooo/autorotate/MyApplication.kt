package com.eiyooooo.autorotate

import android.app.Application
import com.eiyooooo.autorotate.entity.Preferences
import com.eiyooooo.autorotate.util.FLog
import timber.log.Timber
import java.util.Date

lateinit var application: MyApplication private set

class MyApplication : Application() {

    companion object {
        lateinit var appStartTime: Date
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        appStartTime = Date()

        Preferences.init(this)

        FLog.init(this)
        if (Preferences.enableLog) FLog.startFLog()
        Timber.i("App started at: $appStartTime")
    }
}
