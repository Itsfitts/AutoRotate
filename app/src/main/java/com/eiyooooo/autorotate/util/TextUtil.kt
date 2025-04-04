package com.eiyooooo.autorotate.util

import timber.log.Timber

fun String.extractSecondParameter(): String? {
    return try {
        val parts = split("=")
        if (parts.size >= 3) {
            parts[2].substringBefore(',').substringBefore('}').trim()
        } else null
    } catch (e: Exception) {
        Timber.e("Error extracting second parameter from: $this, error: ${e.message}")
        null
    }
}
