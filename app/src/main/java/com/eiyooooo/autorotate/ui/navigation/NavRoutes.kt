package com.eiyooooo.autorotate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eiyooooo.autorotate.R

object NavRoutes {
    const val HOME = "home"
    const val SETTINGS = "settings"
}

@Composable
fun getRouteTitle(route: String): String {
    return when (route) {
        NavRoutes.HOME -> stringResource(R.string.app_name)
        NavRoutes.SETTINGS -> stringResource(R.string.settings)
        else -> stringResource(R.string.app_name)
    }
}

fun shouldShowBackButton(route: String): Boolean {
    return route != NavRoutes.HOME && route != NavRoutes.SETTINGS
}

fun isSettingsRelatedRoute(route: String): Boolean {
    return route.startsWith("settings")
}

fun isSettingsSubRoute(route: String): Boolean {
    return route.startsWith("settings/")
}
