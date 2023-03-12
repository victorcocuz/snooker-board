package com.quickpoint.snookerboard.ui.navigation

import com.quickpoint.snookerboard.domain.utils.MatchState
import com.quickpoint.snookerboard.domain.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.domain.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.ui.navigation.Screen.*
import com.quickpoint.snookerboard.core.utils.Constants

sealed class Screen(val route: String) {
    object Main: Screen(Constants.ID_SCREEN_MAIN)

    object Rules: Screen(Constants.ID_SCREEN_RULES)
    object Game: Screen(Constants.ID_SCREEN_GAME)
    object Summary: Screen(Constants.ID_SCREEN_SUMMARY)

    object DrawerAbout: Screen(Constants.ID_SCREEN_ABOUT)
    object DrawerImprove: Screen(Constants.ID_SCREEN_DRAWER_IMPROVE)
    object DrawerRules: Screen(Constants.ID_SCREEN_DRAWER_RULES)
    object DrawerSettings: Screen(Constants.ID_SCREEN_DRAWER_SETTINGS)
    object DrawerSupport: Screen(Constants.ID_SCREEN_DRAWER_SUPPORT)
}

fun String?.isDrawerRoute() = this in listOf(
    DrawerAbout.route,
    DrawerImprove.route,
    DrawerRules.route,
    DrawerSettings.route,
    DrawerSupport.route
)

fun getRouteFromMatchState(matchState: MatchState) = when (matchState) {
    RULES_IDLE -> Rules.route
    GAME_IN_PROGRESS -> Game.route
    else -> Summary.route
}