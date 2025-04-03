package com.eiyooooo.autorotate.entity

import android.content.ClipboardManager
import androidx.core.content.getSystemService
import com.eiyooooo.autorotate.application

object SystemServices {

    val clipboardManager: ClipboardManager by lazy { application.getSystemService()!! }
}
