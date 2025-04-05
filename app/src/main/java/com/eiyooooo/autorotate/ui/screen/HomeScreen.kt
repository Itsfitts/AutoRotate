package com.eiyooooo.autorotate.ui.screen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eiyooooo.autorotate.R
import com.eiyooooo.autorotate.data.ScreenConfig
import com.eiyooooo.autorotate.data.ScreenConfigRepository
import com.eiyooooo.autorotate.data.getDynamicOrientationOptions
import com.eiyooooo.autorotate.data.getLandscapeOrientationOptions
import com.eiyooooo.autorotate.data.getOrientationName
import com.eiyooooo.autorotate.data.getPortraitOrientationOptions
import com.eiyooooo.autorotate.ui.component.OrientationControlButton
import com.eiyooooo.autorotate.ui.component.OrientationSectionTitle
import com.eiyooooo.autorotate.util.extractSecondParameter
import com.eiyooooo.autorotate.util.getDisplayAddress
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    widthSizeClass: WindowWidthSizeClass,
    showSnackbar: (String) -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { ScreenConfigRepository(context) }
    val scope = rememberCoroutineScope()

    val configs by repository.configs.collectAsState(initial = emptyList())
    var currentOrientation by remember { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) }
    var currentDisplayAddress by remember { mutableStateOf<String?>(null) }
    var currentDisplayName by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val allTabs = listOf(stringResource(R.string.current_screen)) + configs.map { it.displayName }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val view = LocalView.current
    LaunchedEffect(Unit) {
        val currentDisplay = view.display
        val displayAddress = currentDisplay.getDisplayAddress()?.toString() ?: currentDisplay.name
        currentDisplayAddress = displayAddress
        currentDisplayName = displayAddress.extractSecondParameter() ?: displayAddress
        val config = configs.find { it.displayAddress == displayAddress }
        if (config != null) {
            currentOrientation = config.orientation
        }
    }

    LaunchedEffect(selectedTabIndex, configs) {
        if (selectedTabIndex == 0) {
            currentDisplayAddress?.let { displayAddress ->
                val config = configs.find { it.displayAddress == displayAddress }
                currentOrientation = config?.orientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        } else if (selectedTabIndex <= configs.size) {
            val config = configs[selectedTabIndex - 1]
            currentOrientation = config.orientation
            currentDisplayAddress = config.displayAddress
            currentDisplayName = config.displayName
        }
    }

    val onOrientationSelected: (Int) -> Unit = { orientation ->
        currentOrientation = orientation
        if (selectedTabIndex == 0) {
            currentDisplayAddress?.let { displayAddress ->
                currentDisplayName?.let { displayName ->
                    scope.launch {
                        repository.saveConfig(
                            ScreenConfig(
                                displayAddress = displayAddress,
                                displayName = displayName,
                                orientation = orientation
                            )
                        )
                        showSnackbar(context.getString(R.string.saved_orientation, displayName))
                    }
                }
            }
        } else if (selectedTabIndex <= configs.size) {
            val config = configs[selectedTabIndex - 1]
            scope.launch {
                repository.saveConfig(
                    ScreenConfig(
                        displayAddress = config.displayAddress,
                        displayName = config.displayName,
                        orientation = orientation
                    )
                )
                showSnackbar(context.getString(R.string.updated_orientation, config.displayName))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.create_config)) },
            text = {
                Text(stringResource(R.string.create_config_message, currentDisplayName?.let {
                    stringResource(R.string.screen_format, it.extractSecondParameter() ?: it)
                } ?: stringResource(R.string.unknown)))
            },
            confirmButton = {
                TextButton(onClick = {
                    currentDisplayAddress?.let { displayAddress ->
                        currentDisplayName?.let { displayName ->
                            scope.launch {
                                repository.saveConfig(
                                    ScreenConfig(
                                        displayAddress = displayAddress,
                                        displayName = displayName,
                                        orientation = currentOrientation
                                    )
                                )
                                showSnackbar(context.getString(R.string.config_created))
                                selectedTabIndex = allTabs.size - 1
                            }
                        }
                    }
                    showAddDialog = false
                }) {
                    Text(stringResource(R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_config_desc))
            }
        }
    ) { _ ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (configs.isNotEmpty() || selectedTabIndex > 0) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text(stringResource(R.string.current_screen)) }
                        )

                        configs.forEachIndexed { index, config ->
                            Tab(
                                selected = selectedTabIndex == index + 1,
                                onClick = { selectedTabIndex = index + 1 },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.screen_format, config.displayName),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (selectedTabIndex == index + 1) {
                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        repository.deleteConfig(config.displayAddress)
                                                        showSnackbar(context.getString(R.string.config_deleted, config.displayName))
                                                        selectedTabIndex = 0
                                                    }
                                                },
                                                modifier = Modifier.size(16.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = stringResource(R.string.delete),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                when (widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(
                                            R.string.current_screen_format,
                                            if (selectedTabIndex == 0) {
                                                currentDisplayName?.let {
                                                    stringResource(R.string.screen_format, it.extractSecondParameter() ?: it)
                                                } ?: stringResource(R.string.not_detected)
                                            } else stringResource(R.string.screen_format, configs[selectedTabIndex - 1].displayName)
                                        ),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.current_orientation_format, getOrientationName(currentOrientation)),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (currentDisplayAddress != null && currentDisplayAddress == currentDisplayName) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.display_address_missing),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            OrientationLayout(
                                currentOrientation = currentOrientation,
                                onOrientationSelected = onOrientationSelected
                            )
                        }
                    }

                    WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                verticalArrangement = Arrangement.Top
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.current_screen_format,
                                            if (selectedTabIndex == 0) {
                                                currentDisplayName?.let {
                                                    stringResource(R.string.screen_format, it.extractSecondParameter() ?: it)
                                                } ?: stringResource(R.string.not_detected)
                                            } else stringResource(R.string.screen_format, configs[selectedTabIndex - 1].displayName)
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.current_orientation_format, getOrientationName(currentOrientation)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                if (currentDisplayAddress != null && currentDisplayAddress == currentDisplayName) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.display_address_missing),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(if (widthSizeClass == WindowWidthSizeClass.Expanded) 2f else 1.5f)
                                    .fillMaxHeight()
                                    .padding(start = 8.dp)
                            ) {
                                OrientationLayout(
                                    currentOrientation = currentOrientation,
                                    onOrientationSelected = onOrientationSelected
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrientationLayout(
    currentOrientation: Int,
    onOrientationSelected: (Int) -> Unit,
) {
    val dynamicOptions = getDynamicOrientationOptions()
    val landscapeOptions = getLandscapeOrientationOptions()
    val portraitOptions = getPortraitOrientationOptions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OrientationSectionTitle(stringResource(R.string.orientation_dynamic))

        Row(Modifier.fillMaxWidth()) {
            dynamicOptions.forEach { option ->
                OrientationControlButton(
                    option = option,
                    isSelected = currentOrientation == option.orientation,
                    onClick = { onOrientationSelected(option.orientation) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OrientationSectionTitle(stringResource(R.string.orientation_landscape))

        Row(Modifier.fillMaxWidth()) {
            landscapeOptions.forEach { option ->
                OrientationControlButton(
                    option = option,
                    isSelected = currentOrientation == option.orientation,
                    onClick = { onOrientationSelected(option.orientation) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        OrientationSectionTitle(stringResource(R.string.orientation_portrait))

        Row(Modifier.fillMaxWidth()) {
            portraitOptions.forEach { option ->
                OrientationControlButton(
                    option = option,
                    isSelected = currentOrientation == option.orientation,
                    onClick = { onOrientationSelected(option.orientation) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
