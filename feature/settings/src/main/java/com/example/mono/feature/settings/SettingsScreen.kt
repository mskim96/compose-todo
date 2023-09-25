package com.example.mono.feature.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mono.core.designsystem.component.EmptyComingSoon
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.icon.MonoIcons

@Composable
internal fun SettingsRoute(
    onBackClick: () -> Unit
) {
    SettingsScreen(
        onBackClick = onBackClick
    )
}

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        EmptyComingSoon(modifier = Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        titleRes = R.string.settings,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = MonoIcons.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}