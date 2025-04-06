package com.eiyooooo.autorotate.util

import android.view.Display
import android.view.DisplayAddress
import android.view.DisplayInfo
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
private fun <T> getFieldValue(obj: Any, fieldName: String): T? {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(obj) as? T
}

fun Display.getDisplayAddress(): DisplayAddress? {
    return try {
        getFieldValue<DisplayInfo>(this, "mDisplayInfo")?.address ?: return null
    } catch (t: Throwable) {
        Timber.e("getDisplayAddress error: ${t.message}")
        null
    }
}
