package com.quickpoint.snookerboard.compose.navigation

import com.quickpoint.snookerboard.utils.Constants

sealed class Screen(val route: String) {
    object Main: Screen(Constants.ROUTE_SCREEN_MAIN)

    // Main Fragments
    object Rules: Screen(Constants.ROUTE_SCREEN_RULES)
    object Game: Screen(Constants.ROUTE_SCREEN_GAME)
    object Summary: Screen(Constants.ROUTE_SCREEN_SUMMARY)

    // Drawer Fragments
    object DrawerAbout: Screen(Constants.ROUTE_SCREEN_ABOUT)
    object DrawerImprove: Screen(Constants.ROUTE_SCREEN_DRAWER_IMPROVE)
    object DrawerRules: Screen(Constants.ROUTE_SCREEN_DRAWER_RULES)
    object DrawerSettings: Screen(Constants.ROUTE_SCREEN_DRAWER_SETTINGS)
    object DrawerSupport: Screen(Constants.ROUTE_SCREEN_DRAWER_SUPPORT)

    // Dialog Fragments
    object DialogGeneric: Screen (Constants.ROUTE_SCREEN_DIALOG_GENERIC)
    object DialogFoul: Screen(Constants.ROUTE_SCREEN_DIALOG_FOUL)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

fun String?.isDrawerRoute() = this in listOf(
    Screen.DrawerAbout.route,
    Screen.DrawerImprove.route,
    Screen.DrawerRules.route,
    Screen.DrawerSettings.route,
    Screen.DrawerSupport.route
)