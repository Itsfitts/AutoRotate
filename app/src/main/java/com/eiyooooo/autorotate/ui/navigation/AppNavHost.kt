package com.eiyooooo.autorotate.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eiyooooo.autorotate.ui.screen.HomeScreen
import com.eiyooooo.autorotate.ui.screen.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    innerPadding: PaddingValues,
    showSnackbar: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME
        ) {
            composable(route = NavRoutes.HOME) {
                HomeScreen(widthSizeClass, navController, showSnackbar)
            }
            composable(route = NavRoutes.SETTINGS) {
                SettingsScreen(widthSizeClass, showSnackbar)
            }
        }
    }
}
