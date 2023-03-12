package com.quickpoint.snookerboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.ScreenMain
import com.quickpoint.snookerboard.core.billing.PurchaseHelper
import com.quickpoint.snookerboard.ui.fragments.game.ScreenGame
import com.quickpoint.snookerboard.ui.fragments.navdrawer.*
import com.quickpoint.snookerboard.ui.fragments.rules.ScreenRules
import com.quickpoint.snookerboard.ui.fragments.summary.ScreenSummary

@Composable
fun NavGraph(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) { ScreenMain() }
        composable(Screen.Rules.route) { ScreenRules() }
        composable(Screen.Game.route) { ScreenGame() }
        composable(Screen.Summary.route) { ScreenSummary() }
        composable(Screen.DrawerAbout.route) { ScreenDrawerAbout() }
        composable(Screen.DrawerImprove.route) { ScreenDrawerImprove() }
        composable(Screen.DrawerRules.route) { ScreenDrawerRules() }
        composable(Screen.DrawerSettings.route) { ScreenDrawerSettings() }
        composable(Screen.DrawerSupport.route) { ScreenDrawerSupport(purchaseHelper = purchaseHelper) }
    }
}