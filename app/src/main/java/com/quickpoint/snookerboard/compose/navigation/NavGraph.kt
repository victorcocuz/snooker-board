package com.quickpoint.snookerboard.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.quickpoint.snookerboard.FragmentMain
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.fragments.game.FragmentGame
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
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            FragmentMain(navController = navController, mainVm = mainVm, dataStore = dataStore)
        }
        composable(route = Screen.RulesScreen.route) {
            FragmentRules(mainVm = mainVm, dataStore = dataStore)
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
            FragmentDrawerSettings(mainVm = mainVm)
        }
        composable(route = Screen.DrawerSupportScreen.route) {
            FragmentDrawerSupport(navController = navController, purchaseHelper = purchaseHelper)
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