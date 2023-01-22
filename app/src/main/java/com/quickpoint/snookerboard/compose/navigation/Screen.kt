package com.quickpoint.snookerboard.compose.navigation

sealed class Screen(val route: String) {
    object Main: Screen("main_screen")

    // Main Fragments
    object Rules: Screen("screen_rules")
    object Game: Screen("screen_fragment_game")
    object Summary: Screen("screen_fragment_summary")

    // Drawer Fragments
    object DrawerAbout: Screen("screen_drawer_about")
    object DrawerImprove: Screen("screen_drawer_improve")
    object DrawerRules: Screen("screen_drawer_rules")
    object DrawerSettings: Screen("screen_drawer_settings")
    object DrawerSupport: Screen("screen_drawer_support")

    // Dialog Fragments
    object DialogGeneric: Screen ("screen_dialog_generic")
    object DialogFoul: Screen("screen_dialog_foul")

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