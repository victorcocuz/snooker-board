package com.quickpoint.snookerboard.compose.navigation

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R

class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: Painter,
)

@Composable
fun getMenuItems() = listOf(
    MenuItem(
        id = "id_drawer_rules",
        title = stringResource(R.string.menu_drawer_rules),
        contentDescription = stringResource(R.string.menu_drawer_rules),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_rules)
    ),
    MenuItem(
        id = "id_drawer_improve",
        title = stringResource(R.string.menu_drawer_improve),
        contentDescription = stringResource(R.string.menu_drawer_improve),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_improve)
    ),
    MenuItem(
        id = "id_drawer_support",
        title = stringResource(R.string.menu_drawer_support),
        contentDescription = stringResource(R.string.menu_drawer_support),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_support)
    ),
    MenuItem(
        id = "id_drawer_settings",
        title = stringResource(R.string.menu_drawer_settings),
        contentDescription = stringResource(R.string.menu_drawer_settings),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_settings)
    ),
    MenuItem(
        id = "id_drawer_about",
        title = stringResource(R.string.menu_drawer_about),
        contentDescription = stringResource(R.string.menu_drawer_about),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_about)
    )
)

@Composable
fun Whatever() =
    Icon(painter = painterResource(id = R.drawable.ic_temp_menu_nav_rules), contentDescription = null)
