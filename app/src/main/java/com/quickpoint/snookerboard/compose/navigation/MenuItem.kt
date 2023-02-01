package com.quickpoint.snookerboard.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.utils.Constants

class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: Painter
)

@Composable
fun getMenuItems() = listOf(
    MenuItem(
        id = Constants.NAV_ID_DRAWER_RULES,
        title = stringResource(R.string.menu_drawer_rules),
        contentDescription = stringResource(R.string.menu_drawer_rules),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_rules)
    ),
    MenuItem(
        id = Constants.NAV_ID_DRAWER_IMPROVE,
        title = stringResource(R.string.menu_drawer_improve),
        contentDescription = stringResource(R.string.menu_drawer_improve),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_improve)
    ),
    MenuItem(
        id = Constants.NAV_ID_DRAWER_SUPPORT,
        title = stringResource(R.string.menu_drawer_support),
        contentDescription = stringResource(R.string.menu_drawer_support),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_support)
    ),
    MenuItem(
        id = Constants.NAV_ID_DRAWER_SETTINGS,
        title = stringResource(R.string.menu_drawer_settings),
        contentDescription = stringResource(R.string.menu_drawer_settings),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_settings)
    ),
    MenuItem(
        id = Constants.NAV_ID_DRAWER_ABOUT,
        title = stringResource(R.string.menu_drawer_about),
        contentDescription = stringResource(R.string.menu_drawer_about),
        icon = painterResource(id = R.drawable.ic_temp_menu_nav_about)
    )
)
