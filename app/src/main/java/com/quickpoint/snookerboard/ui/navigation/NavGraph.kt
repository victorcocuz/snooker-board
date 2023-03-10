package com.quickpoint.snookerboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.ScreenMain
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.ui.fragments.game.ScreenGame
import com.quickpoint.snookerboard.ui.fragments.navdrawer.*
import com.quickpoint.snookerboard.ui.fragments.rules.ScreenRules
import com.quickpoint.snookerboard.ui.fragments.summary.ScreenSummary
import com.quickpoint.snookerboard.utils.DataStore

@Composable
fun NavGraph(
    navController: NavHostController,
    mainVm: MainViewModel,
    dataStore: DataStore,
    purchaseHelper: PurchaseHelper,
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) { ScreenMain(mainVm = mainVm, dataStore = dataStore) }
        composable(Screen.Rules.route) { ScreenRules(mainVm = mainVm, dataStore = dataStore) }
        composable(Screen.Game.route) { ScreenGame(mainVm = mainVm, dataStore = dataStore) }
        composable(Screen.Summary.route) { ScreenSummary(mainVm = mainVm) }
        composable(Screen.DrawerAbout.route) { ScreenDrawerAbout() }
        composable(Screen.DrawerImprove.route) { ScreenDrawerImprove() }
        composable(Screen.DrawerRules.route) { ScreenDrawerRules() }
        composable(Screen.DrawerSettings.route) { ScreenDrawerSettings(mainVm = mainVm, dataStore = dataStore) }
        composable(Screen.DrawerSupport.route) { ScreenDrawerSupport(purchaseHelper = purchaseHelper) }
    }
}