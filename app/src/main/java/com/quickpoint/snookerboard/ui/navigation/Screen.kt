package com.quickpoint.snookerboard.ui.navigation

import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.ui.navigation.Screen.*
import com.quickpoint.snookerboard.utils.Constants

sealed class Screen(val route: String) {
    object Main: Screen(Constants.ID_SCREEN_MAIN)

    // Main Fragments
    object Rules: Screen(Constants.ID_SCREEN_RULES)
    object Game: Screen(Constants.ID_SCREEN_GAME)
    object Summary: Screen(Constants.ID_SCREEN_SUMMARY)

    // Drawer Fragments
    object DrawerAbout: Screen(Constants.ID_SCREEN_ABOUT)
    object DrawerImprove: Screen(Constants.ID_SCREEN_DRAWER_IMPROVE)
    object DrawerRules: Screen(Constants.ID_SCREEN_DRAWER_RULES)
    object DrawerSettings: Screen(Constants.ID_SCREEN_DRAWER_SETTINGS)
    object DrawerSupport: Screen(Constants.ID_SCREEN_DRAWER_SUPPORT)

    // Dialog Fragments
    object DialogGeneric: Screen(Constants.ID_SCREEN_DIALOG_GENERIC)
    object DialogFoul: Screen(Constants.ID_SCREEN_DIALOG_FOUL)

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
    DrawerAbout.route,
    DrawerImprove.route,
    DrawerRules.route,
    DrawerSettings.route,
    DrawerSupport.route
)

fun getRouteFromMatchState(matchState: MatchState) = when (matchState) {
    RULES_IDLE, RULES_PENDING -> Rules.route
    GAME_IN_PROGRESS, GAME_SAVED -> Game.route
    else -> Summary.route
}