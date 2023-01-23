package com.quickpoint.snookerboard.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.FragmentMain
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.fragments.game.FragmentGame
import com.quickpoint.snookerboard.fragments.gamedialogs.FragmentDialogFoul
import com.quickpoint.snookerboard.fragments.navdrawer.*
import com.quickpoint.snookerboard.fragments.rules.FragmentRules
import com.quickpoint.snookerboard.fragments.summary.FragmentSummary
import com.quickpoint.snookerboard.utils.DataStore

@Composable
fun NavGraph(
    navController: NavHostController,
    mainVm: MainViewModel,
    dataStore: DataStore,
    purchaseHelper: PurchaseHelper
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            FragmentMain(navController = navController, mainVm = mainVm, dataStore = dataStore)
        }

        // Main Fragments
        composable(route = Screen.Rules.route) {
            FragmentRules(navController = navController, mainVm = mainVm, dataStore = dataStore)
        }
        composable(route = Screen.Game.route) {
            FragmentGame(navController = navController, mainVm = mainVm, dataStore = dataStore)
        }
        composable(route = Screen.Summary.route) {
            FragmentSummary(navController = navController, mainVm = mainVm)
        }

        // Drawer Fragments
        composable(route = Screen.DrawerAbout.route) {
            FragmentDrawerAbout(navController = navController)
        }
        composable(route = Screen.DrawerImprove.route) {
            FragmentDrawerImprove(navController = navController)
        }
        composable(route = Screen.DrawerRules.route) {
            FragmentDrawerRules(navController = navController)
        }
        composable(route = Screen.DrawerSettings.route) {
            FragmentDrawerSettings(mainVm = mainVm)
        }
        composable(route = Screen.DrawerSupport.route) {
            FragmentDrawerSupport(navController = navController, purchaseHelper = purchaseHelper)
        }

        // Dialog Fragments
//        composable(route = Screen.DialogGeneric.route) {
//            FragmentDialogGeneric(navController = navController)
//        }
        composable(route = Screen.DialogFoul.route) {
            FragmentDialogFoul(navController = navController)
        }
    }
}

//@Composable
//fun MainScreen(navController: NavController) {
//    var text by remember { mutableStateOf("") }
//    Column(
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 50.dp)
//    ) {
//        TextField(
//            value = text, onValueChange = {
//                text = it
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = {
//                navController.navigate(Screen.DetailScreen.withArgs(text))
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text(text = "To DetailScreen")
//        }
//    }
//}
//
//@Composable
//fun DetailScreen(name: String?) {
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text("hello $name")
//    }
//}