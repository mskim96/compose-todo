package com.example.mono.feature.calendar

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
internal fun CalendarRoute(
    openDrawer: () -> Unit
) {
    CalendarScreen(
        openDrawer = openDrawer
    )
}

@Composable
internal fun CalendarScreen(
    openDrawer: () -> Unit
) {
    Scaffold(
        topBar = {
            CalendarTopAppBar(
                openDrawer = openDrawer
            )
        }
    ) { padding ->
        EmptyComingSoon(modifier = Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopAppBar(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        titleRes = R.string.calendar,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = MonoIcons.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )
}