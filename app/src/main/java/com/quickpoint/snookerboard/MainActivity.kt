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
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player02
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.utils.Constants.NAV_ID_DRAWER_ABOUT
import com.quickpoint.snookerboard.utils.Constants.NAV_ID_DRAWER_IMPROVE
import com.quickpoint.snookerboard.utils.Constants.NAV_ID_DRAWER_RULES
import com.quickpoint.snookerboard.utils.Constants.NAV_ID_DRAWER_SETTINGS
import com.quickpoint.snookerboard.utils.Constants.NAV_ID_DRAWER_SUPPORT
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
fun SnookerBoardApp(activity: MainActivity, splashScreen: androidx.core.splashscreen.SplashScreen) {
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

    SnookerBoardTheme {
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
                                NAV_ID_DRAWER_RULES -> Screen.DrawerRules.route
                                NAV_ID_DRAWER_IMPROVE -> Screen.DrawerImprove.route
                                NAV_ID_DRAWER_SUPPORT -> Screen.DrawerSupport.route
                                NAV_ID_DRAWER_SETTINGS -> Screen.DrawerSettings.route
                                NAV_ID_DRAWER_ABOUT -> Screen.DrawerAbout.route
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
        dataStore.loadPreferences()
        Player01.assignDataStore(dataStore)
        Player02.assignDataStore(dataStore)
        Settings.dataStore = dataStore
        mainVm.loadMatchIfSaved()
        mainVm.onEmit(MatchAction.NAV_TO_PLAY)
    }
}
