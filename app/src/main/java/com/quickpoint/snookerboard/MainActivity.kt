package com.quickpoint.snookerboard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.ads.MobileAds
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.compose.navigation.*
import com.quickpoint.snookerboard.compose.ui.styles.DefaultSnackbar
import com.quickpoint.snookerboard.compose.ui.styles.GenericSurface
import com.quickpoint.snookerboard.compose.ui.theme.Green
import com.quickpoint.snookerboard.compose.ui.theme.SnookerBoardTheme
import com.quickpoint.snookerboard.compose.ui.theme.Transparent
import com.quickpoint.snookerboard.domain.objects.DomainPlayer
import com.quickpoint.snookerboard.domain.objects.MatchSettings
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.MatchAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Keep splash screen on until match loading check is complete
        super.onCreate(savedInstanceState) // Create view after installing splash screen
        setContent {
            SnookerBoardApp(this, splashScreen)
        }
    }
}

@Composable
fun SnookerBoardApp(activity: MainActivity, splashScreen: SplashScreen) {
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val mainVm: MainViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    splashScreen.setKeepOnScreenCondition { mainVm.keepSplashScreen.value }
    val systemUiController = rememberSystemUiController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val purchaseHelper = PurchaseHelper(activity)
    purchaseHelper.billingSetup()

    SnookerBoardTheme() {
        GenericSurface {
            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = { scaffoldState.snackbarHostState },
                topBar = {
                    AppBar(navController = navController, onNavigationIconClick = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    })
                },
                drawerContent = {
                    DrawerHeader()
                    DrawerBody(items = getMenuItems(), onItemClick = {
                        navController.navigate(
                            when (it.id) {
                                "id_drawer_rules" -> Screen.DrawerRules.route
                                "id_drawer_improve" -> Screen.DrawerImprove.route
                                "id_drawer_support" -> Screen.DrawerSupport.route
                                "id_drawer_settings" -> Screen.DrawerSettings.route
                                "id_drawer_about" -> Screen.DrawerAbout.route
                                else -> Screen.Rules.route // Unused
                            }
                        )
                        coroutineScope.launch {
                            delay(300)
                            scaffoldState.drawerState.close()
                        }
                    })
                },
                backgroundColor = Transparent,
                drawerBackgroundColor = Green
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NavGraph(navController, mainVm, dataStore, purchaseHelper)
                    DefaultSnackbar(
                        snackbarHostState = scaffoldState.snackbarHostState,
                        onDismiss = { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    LaunchedEffect(key1 = true) {
                        MobileAds.initialize(context) // AdMob
                        mainVm.eventSharedFlow.collect { event ->
                            when (event) {
                                is ScreenEvents.ShowSnackbar -> {
                                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                                }
                                is ScreenEvents.Navigate -> {
                                    navController.navigate(event.route)
                                }
                                else -> {} // Not Implemented
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FragmentMain(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    LaunchedEffect(key1 = true) {
        DomainPlayer.Player01.assignDataStore(dataStore)
        DomainPlayer.Player02.assignDataStore(dataStore)
        MatchSettings.Settings.dataStore = dataStore
        mainVm.loadMatchIfSaved()

        mainVm.onEmit(MatchAction.NAV_TO_PLAY)
    }
}
