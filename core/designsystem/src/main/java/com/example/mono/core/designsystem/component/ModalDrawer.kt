package com.example.mono.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Mono drawer sheet with content slots Wraps Material 3 [ModalDrawerSheet].
 *
 * @param modifier Modifier be applied to be modal drawer sheet.
 * @param content Drawer sheet content slot.
 */
@Composable
fun MonoModalSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalDrawerSheet(
        modifier = modifier,
        content = content,
        drawerContainerColor = MonoDrawerDefaults.drawerContainerColor(),
        drawerContentColor = MonoDrawerDefaults.drawerContentColor()
    )
}

/**
 * Mono navigation drawer item with icon and label content slots. Wraps Material 3
 * [NavigationDrawerItem].
 *
 * @param label The item text label content.
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param modifier Modifier to be applied to this item.
 * @param icon The item icon content.
 * @param badge The icon badge content.
 */
@Composable
fun MonoNavigationDrawerItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    badge: @Composable (() -> Unit)? = null
) {
    NavigationDrawerItem(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        badge = badge,
        shape = MonoDrawerDefaults.drawerSelectedContainerShape(),
        colors = NavigationDrawerItemDefaults.colors(
            selectedTextColor = MonoDrawerDefaults.drawerSelectedItemColor(),
            unselectedTextColor = MonoDrawerDefaults.drawerContentColor(),
            selectedIconColor = MonoDrawerDefaults.drawerSelectedItemColor(),
            unselectedIconColor = MonoDrawerDefaults.drawerContentColor(),
            selectedContainerColor = MonoDrawerDefaults.drawerSelectedContainerColor(),
            unselectedContainerColor = MonoDrawerDefaults.drawerContainerColor()
        ),
    )
}

/**
 * Mono modal navigation drawer with drawer content and content slots. Wraps Material 3
 * [ModalNavigationDrawer].
 *
 * @param drawerContent drawer content slots.
 * @param modifier Modifier to be applied to this drawer.
 * @param drawerState State of the drawer.
 * @param content content slots.
 */
@Composable
fun MonoModalNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = drawerContent,
        modifier = modifier,
        drawerState = drawerState,
        content = content
    )
}

/**
 * Mono drawer default values.
 */
object MonoDrawerDefaults {
    @Composable
    fun drawerContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun drawerSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun drawerContainerColor() = MaterialTheme.colorScheme.surface

    @Composable
    fun drawerSelectedContainerColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun drawerSelectedContainerShape() = MaterialTheme.shapes.small
}