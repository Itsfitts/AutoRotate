package com.eiyooooo.autorotate.entity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.eiyooooo.autorotate.BuildConfig
import com.eiyooooo.autorotate.util.get
import com.eiyooooo.autorotate.util.put
import com.fredporciuncula.flow.preferences.FlowSharedPreferences

object Preferences {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var flowSharedPreferences: FlowSharedPreferences

    fun init(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        this.sharedPreferences = sharedPreferences
        this.flowSharedPreferences = FlowSharedPreferences(sharedPreferences)

        if (!sharedPreferences.contains("others.enable_log")) {
            enableLog = BuildConfig.DEBUG
        }
    }

    var systemColor
        get() = sharedPreferences.get("systemColor", true)
        set(value) = sharedPreferences.put("systemColor", value)

    val systemColorFlow
        get() = flowSharedPreferences.getBoolean("systemColor", true).asFlow()

    var darkTheme
        get() = sharedPreferences.get("darkTheme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = sharedPreferences.put("darkTheme", value)

    val darkThemeFlow
        get() = flowSharedPreferences.getInt("darkTheme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).asFlow()

    var enableLog
        get() = sharedPreferences.get("enable_log", BuildConfig.DEBUG)
        set(value) = sharedPreferences.put("enable_log", value)

    val enableLogFlow
        get() = flowSharedPreferences.getBoolean("enable_log", BuildConfig.DEBUG).asFlow()
}
