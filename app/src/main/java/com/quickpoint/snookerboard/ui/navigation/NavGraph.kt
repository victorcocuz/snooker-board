package com.quickpoint.snookerboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.ScreenMain
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.ui.fragments.game.ScreenGame
import com.quickpoint.snookerboard.ui.fragments.navdrawer.*
import com.quickpoint.snookerboard.ui.fragments.rules.ScreenRules
import com.quickpoint.snookerboard.ui.fragments.summary.ScreenSummary
import com.quickpoint.snookerboard.utils.DataStore

@Composable
fun NavGraph(
    navController: NavHostController,
    mainVm: MainViewModel,
    gameVm: GameViewModel,
    dataStore: DataStore,
    purchaseHelper: PurchaseHelper
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            ScreenMain(mainVm = mainVm, dataStore = dataStore)
        }

        // Main Fragments
        composable(route = Screen.Rules.route) {
            ScreenRules(navController = navController, mainVm = mainVm, dataStore = dataStore)
        }
        composable(route = Screen.Game.route) {
            ScreenGame(navController = navController, mainVm = mainVm, gameVm = gameVm, dataStore = dataStore)
        }
        composable(route = Screen.Summary.route) {
            ScreenSummary(navController = navController, mainVm = mainVm)
        }

        // Drawer Fragments
        composable(route = Screen.DrawerAbout.route) {
            ScreenDrawerAbout(navController = navController)
        }
        composable(route = Screen.DrawerImprove.route) {
            ScreenDrawerImprove(navController = navController)
        }
        composable(route = Screen.DrawerRules.route) {
            ScreenDrawerRules(navController = navController)
        }
        composable(route = Screen.DrawerSettings.route) {
            ScreenDrawerSettings(dataStore)
        }
        composable(route = Screen.DrawerSupport.route) {
            ScreenDrawerSupport(navController = navController, purchaseHelper = purchaseHelper)
        }
    }
}