package com.eiyooooo.autorotate.ui.screen

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eiyooooo.autorotate.BuildConfig
import com.eiyooooo.autorotate.R
import com.eiyooooo.autorotate.entity.Preferences
import com.eiyooooo.autorotate.ui.component.SettingClickableItem
import com.eiyooooo.autorotate.ui.component.SettingDropdownItem
import com.eiyooooo.autorotate.ui.component.SettingSwitchItem
import com.eiyooooo.autorotate.util.FLog

@Composable
fun OtherSettingsContent() {
    val context = LocalContext.current

    val systemColor by Preferences.systemColorFlow.collectAsState(initial = Preferences.systemColor)
    val darkTheme by Preferences.darkThemeFlow.collectAsState(initial = Preferences.darkTheme)
    val enableLog by Preferences.enableLogFlow.collectAsState(initial = Preferences.enableLog)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingSwitchItem(
            title = context.getString(R.string.use_system_color),
            checked = systemColor,
            onCheckedChange = {
                Preferences.systemColor = it
                (context as? Activity)?.recreate()
            },
            isFirst = true
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        val darkThemeList = listOf(
            context.getString(R.string.follow_system),
            context.getString(R.string.always_off),
            context.getString(R.string.always_on)
        )
        SettingDropdownItem(
            title = context.getString(R.string.dark_theme),
            currentValue = darkThemeList[darkTheme.coerceAtLeast(0)],
            options = darkThemeList,
            onValueChange = {
                Preferences.darkTheme = when (it) {
                    context.getString(R.string.follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    context.getString(R.string.always_off) -> AppCompatDelegate.MODE_NIGHT_NO
                    context.getString(R.string.always_on) -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> darkTheme
                }
                AppCompatDelegate.setDefaultNightMode(Preferences.darkTheme)
                (context as? Activity)?.recreate()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SettingSwitchItem(
            title = context.getString(R.string.enable_log),
            checked = enableLog,
            onCheckedChange = {
                if (it) {
                    FLog.startFLog()
                } else {
                    FLog.stopFLog()
                }
                Preferences.enableLog = it
            },
            isLast = !enableLog
        )

        AnimatedVisibility(
            visible = enableLog,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingClickableItem(
                    title = context.getString(R.string.export_logs),
                    onClick = {
                        FLog.exportLogs(context)
                    },
                    isLast = enableLog
                )
            }
        }
    }
}

@Composable
fun AboutContent(showSnackbar: (String) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingClickableItem(
            title = stringResource(R.string.version) + BuildConfig.VERSION_NAME,
            onClick = {
                showSnackbar(context.getString(R.string.version_tips))
            },
            isFirst = true,
            isLast = true
        )
    }
}

@Composable
fun SettingsScreen(
    widthSizeClass: WindowWidthSizeClass,
    showSnackbar: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    OtherSettingsContent()
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutContent(showSnackbar)
                }
            }

            WindowWidthSizeClass.Medium -> {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    OtherSettingsContent()
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutContent(showSnackbar)
                }
            }

            WindowWidthSizeClass.Expanded -> {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        OtherSettingsContent()
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        AboutContent(showSnackbar)
                    }
                }
            }
        }
    }
}
