package com.eiyooooo.autorotate.data

import android.content.pm.ActivityInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.ScreenRotationAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.StayPrimaryLandscape
import androidx.compose.material.icons.filled.StayPrimaryPortrait
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.eiyooooo.autorotate.R

data class OrientationOption(
    val orientation: Int,
    val icon: ImageVector,
    val textResId: Int
)

@Composable
fun getDynamicOrientationOptions(): List<OrientationOption> {
    return listOf(
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            icon = Icons.Default.Smartphone,
            textResId = R.string.orientation_default
        ),
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
            icon = Icons.Default.ScreenRotation,
            textResId = R.string.orientation_full_sensor
        )
    )
}

@Composable
fun getLandscapeOrientationOptions(): List<OrientationOption> {
    return listOf(
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            icon = Icons.Default.StayPrimaryLandscape,
            textResId = R.string.orientation_landscape
        ),
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            icon = Icons.AutoMirrored.Filled.RotateLeft,
            textResId = R.string.orientation_reverse_landscape
        ),
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            icon = Icons.Default.ScreenRotationAlt,
            textResId = R.string.orientation_sensor_landscape
        )
    )
}

@Composable
fun getPortraitOrientationOptions(): List<OrientationOption> {
    return listOf(
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            icon = Icons.Default.StayPrimaryPortrait,
            textResId = R.string.orientation_portrait
        ),
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            icon = Icons.AutoMirrored.Filled.RotateRight,
            textResId = R.string.orientation_reverse_portrait
        ),
        OrientationOption(
            orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            icon = Icons.Outlined.ScreenRotation,
            textResId = R.string.orientation_sensor_portrait
        )
    )
}

@Composable
fun getOrientationName(orientation: Int): String {
    return when (orientation) {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> stringResource(R.string.orientation_default)
        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> stringResource(R.string.orientation_full_sensor)
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> stringResource(R.string.orientation_landscape)
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> stringResource(R.string.orientation_portrait)
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> stringResource(R.string.orientation_reverse_landscape)
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> stringResource(R.string.orientation_reverse_portrait)
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> stringResource(R.string.orientation_sensor_landscape)
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> stringResource(R.string.orientation_sensor_portrait)
        else -> stringResource(R.string.orientation_unknown)
    }
}
