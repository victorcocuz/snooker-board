package com.quickpoint.snookerboard.compose.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.fragments.game.FragmentGame
import com.quickpoint.snookerboard.fragments.navdrawer.FragmentDrawerAbout
import com.quickpoint.snookerboard.fragments.navdrawer.FragmentDrawerImprove
import com.quickpoint.snookerboard.fragments.navdrawer.FragmentDrawerRules
import com.quickpoint.snookerboard.fragments.navdrawer.FragmentDrawerSettings
import com.quickpoint.snookerboard.fragments.navdrawer.FragmentDrawerSupport
import com.quickpoint.snookerboard.fragments.rules.FragmentRules
import com.quickpoint.snookerboard.fragments.summary.FragmentSummary

@Composable
fun NavGraph(
    navController: NavHostController,
    mainVm: MainViewModel,
    purchaseHelper: PurchaseHelper
) {
    NavHost(navController = navController, startDestination = Screen.RulesScreen.route) {
        composable(route = Screen.RulesScreen.route) {
            FragmentRules(navController = navController, mainVm = mainVm)
        }
        composable(route = Screen.GameScreen.route) {
            FragmentGame(navController = navController, mainVm = mainVm)
        }
        composable(route = Screen.SummaryScreen.route) {
            FragmentSummary(navController = navController, mainVm = mainVm)
        }
        composable(route = Screen.DrawerAboutScreen.route) {
            FragmentDrawerAbout(navController = navController)
        }
        composable(route = Screen.DrawerImproveScreen.route) {
            FragmentDrawerImprove(navController = navController)
        }
        composable(route = Screen.DrawerRulesScreen.route) {
            FragmentDrawerRules(navController = navController)
        }
        composable(route = Screen.DrawerSettingsScreen.route) {
            FragmentDrawerSettings(navController = navController, mainVm = mainVm)
        }
        composable(route = Screen.DrawerSupportScreen.route) {
            FragmentDrawerSupport(navController = navController, purchaseHelper = purchaseHelper)
        }
//        composable(route = Screen.MainScreen.route) {
//            MainScreen(navController = navController)
//        }
//        composable(
//            route = Screen.DetailScreen.route + "/{name}",
//            arguments = listOf(
//                navArgument("name") {
//                    type = NavType.StringType
//                    defaultValue = "Victor"
//                    nullable = true
//                }
//            )
//        ) { navBackStackEntry ->
//            DetailScreen(name = navBackStackEntry.arguments?.getString("name"))
//        }

    }
}

@Composable
fun MainScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        TextField(
            value = text, onValueChange = {
                text = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.navigate(Screen.DetailScreen.withArgs(text))
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "To DetailScreen")
        }
    }
}

@Composable
fun DetailScreen(name: String?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("hello $name")
    }
}