package com.quickpoint.snookerboard.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.components.IconArrowBack
import com.quickpoint.snookerboard.ui.components.IconDefault
import com.quickpoint.snookerboard.ui.components.IconMenu
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.Transparent

@Composable
fun AppBar(
    navController: NavController,
    onNavigationIconClick: () -> Unit,
    onMenuItemClick: (MenuItem) -> Unit,
    actionItems: List<MenuItem>,
    actionItemsOverflow: List<MenuItem>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(title = { stringResource(id = R.string.app_name) },
        backgroundColor = Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        elevation = 0.dp,
        navigationIcon = {
            if (currentRoute.isDrawerRoute()) IconButton(onClick = { navController.navigateUp() }) { IconArrowBack() }
            else if (currentRoute == Screen.Rules.route) IconButton(onClick = onNavigationIconClick) { IconMenu() }
        },
        actions = {
            var showMenu by remember { mutableStateOf(false) }

            ActionMenuBody(
                items = actionItems,
                onShowMenuClick = { showMenu = !showMenu },
                onItemClick = onMenuItemClick
            )

            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                expanded = showMenu,
                onDismissRequest = { showMenu = false }) {
                ActionMenuOverflowBody(
                    items = actionItemsOverflow,
                    onItemClick = { item ->
                        onMenuItemClick(item)
                        showMenu = false
                    })
            }
        })
}

@Composable
fun ActionMenuBody(
    items: List<MenuItem>,
    onShowMenuClick: () -> Unit,
    onItemClick: (MenuItem) -> Unit,
) {
    for (item in items) {
        IconButton(
            modifier = Modifier.alpha(if (item.isActive) 1f else 0.5f),
            onClick = {
                if (item.id == MenuItemIds.ID_MENU_ITEM_MORE) onShowMenuClick()
                else onItemClick(item)
            }) { IconDefault(imageVector = item.imageVector, contentDescription = item.contentDescription) }
    }
}

@Composable
fun ActionMenuOverflowBody(
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
) {
    for (item in items) {
        DropdownMenuItem(
            modifier = Modifier.alpha(if (item.isActive) 1f else 0.5f),
            onClick = { onItemClick(item) }) {
            IconDefault(imageVector = item.imageVector, contentDescription = item.contentDescription)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item.title, modifier = Modifier.weight(1f), color = BrownDark)
        }
    }
}