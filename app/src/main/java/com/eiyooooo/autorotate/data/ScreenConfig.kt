package com.eiyooooo.autorotate.data

import android.content.pm.ActivityInfo
import kotlinx.serialization.Serializable

@Serializable
data class ScreenConfig(
    val displayAddress: String,
    val displayName: String,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
)
