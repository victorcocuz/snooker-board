package com.quickpoint.snookerboard.compose.navigation

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object RulesScreen: Screen("rules_screen")
    object GameScreen: Screen("game_screen")
    object SummaryScreen: Screen("summary_screen")
    object DrawerAboutScreen: Screen("drawer_about_screen")
    object DrawerImproveScreen: Screen("drawer_improve_screen")
    object DrawerRulesScreen: Screen("drawer_rules_screen")
    object DrawerSettingsScreen: Screen("drawer_settings_screen")
    object DrawerSupportScreen: Screen("drawer_support_screen")

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
    Screen.DrawerAboutScreen.route,
    Screen.DrawerImproveScreen.route,
    Screen.DrawerRulesScreen.route,
    Screen.DrawerSettingsScreen.route,
    Screen.DrawerSupportScreen.route
)