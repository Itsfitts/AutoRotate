package com.eiyooooo.autorotate.ui.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.eiyooooo.autorotate.R
import com.eiyooooo.autorotate.application
import com.eiyooooo.autorotate.data.ShizukuStatus
import timber.log.Timber

@Composable
fun ShizukuCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation,
    showSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val shizukuStatus by application.shizukuServiceManager.shizukuStatus.collectAsState()
    val serviceEnabled by application.shizukuServiceManager.serviceEnabled.collectAsState()

    LaunchedEffect(Unit) {
        application.shizukuServiceManager.checkShizukuPermission()
    }

    when (shizukuStatus) {
        ShizukuStatus.HAVE_PERMISSION -> {
            ShizukuContentCard(
                modifier = modifier,
                elevation = elevation,
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_connected),
                showDetail = true,
                detail = "",
                showSwitch = true,
                switchChecked = serviceEnabled,
                onSwitchChanged = { application.shizukuServiceManager.setServiceEnabled(it) },
                switchLabel = stringResource(R.string.enable_service),
                onClick = null
            )
        }

        ShizukuStatus.NO_PERMISSION -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_authorization_instruction),
                onClick = {
                    application.shizukuServiceManager.requestPermission()
                },
                modifier = modifier,
                elevation = elevation
            )
        }

        ShizukuStatus.VERSION_NOT_SUPPORT -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_need_update),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            addCategory(Intent.CATEGORY_BROWSABLE)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = "https://shizuku.rikka.app/zh-hans/guide/setup/".toUri()
                        }
                        context.startActivity(intent)
                    } catch (t: Throwable) {
                        Timber.e(t, "Open Shizuku instruction failed")
                        showSnackbar(context.getString(R.string.no_browser))
                    }
                },
                modifier = modifier,
                elevation = elevation
            )
        }

        ShizukuStatus.SHIZUKU_NOT_RUNNING -> {
            ShizukuContentCard(
                icon = R.drawable.shizuku,
                title = stringResource(R.string.Shizuku_explanation),
                showDetail = true,
                detail = stringResource(R.string.Shizuku_setup_instruction),
                onClick = {
                    val shizukuPackageName = "moe.shizuku.privileged.api"
                    var appLaunched = false
                    if (context.isPackageInstalled(shizukuPackageName)) {
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(shizukuPackageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            } else {
                                throw RuntimeException("Cannot launch Shizuku app")
                            }
                        } catch (t: Throwable) {
                            Timber.e(t, "Open Shizuku app failed")
                        }
                        appLaunched = true
                    }
                    if (!appLaunched) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                addCategory(Intent.CATEGORY_BROWSABLE)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                data = "https://shizuku.rikka.app/zh-hans/guide/setup/".toUri()
                            }
                            context.startActivity(intent)
                        } catch (t: Throwable) {
                            Timber.e(t, "Open Shizuku instruction failed")
                            showSnackbar(context.getString(R.string.no_browser))
                        }
                    }
                },
                modifier = modifier,
                elevation = elevation
            )
        }
    }
}

private fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (t: Throwable) {
        false
    }
}

@Composable
private fun ShizukuContentCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation,
    icon: Int,
    title: String,
    showDetail: Boolean,
    detail: String,
    showSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChanged: ((Boolean) -> Unit)? = null,
    switchLabel: String = "",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = elevation
    ) {
        Column {
            Row(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showDetail && detail.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Text(
                    modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (showSwitch) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Column(
                    modifier = Modifier
                        .clickable { onSwitchChanged?.invoke(!switchChecked) },
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = switchLabel,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = switchChecked,
                            onCheckedChange = onSwitchChanged
                        )
                    }
                }
            }
        }
    }
}
