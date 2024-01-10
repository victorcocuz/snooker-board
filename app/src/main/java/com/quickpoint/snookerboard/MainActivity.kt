package com.quickpoint.snookerboard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.core.admob.loadInterstitialAd
import com.quickpoint.snookerboard.core.billing.PurchaseHelper
import com.quickpoint.snookerboard.core.utils.getSnackText
import com.quickpoint.snookerboard.ui.components.DefaultSnackbar
import com.quickpoint.snookerboard.ui.navigation.*
import com.quickpoint.snookerboard.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var purchaseHelper: PurchaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { SnookerBoardApp(purchaseHelper, splashScreen) }
    }
}

@Composable
fun SnookerBoardApp(purchaseHelper: PurchaseHelper, splashScreen: SplashScreen) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val mainVm = hiltViewModel<MainViewModel>()
    splashScreen.setKeepOnScreenCondition { mainVm.keepSplashScreen.value }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    purchaseHelper.billingSetup()

    SnookerBoardTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(GreenBright, GreenBrighter))),
            color = Color.Transparent
        ) {
            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = { scaffoldState.snackbarHostState },
                topBar = {
                    val onMenuItemSelectedClick by mainVm.actionItemOnClick.collectAsState()
                    val actionItems by mainVm.actionItems.collectAsState()
                    val actionItemsOverflow by mainVm.actionItemsOverflow.collectAsState()
                    AppBar(
                        navController = navController,
                        onNavigationIconClick = { coroutineScope.launch { scaffoldState.drawerState.open() } },
                        onMenuItemClick = onMenuItemSelectedClick,
                        actionItems = actionItems,
                        actionItemsOverflow = actionItemsOverflow
                    )
                },
                drawerContent = {
                    DrawerHeader()
                    DrawerBody(items = getMenuItems(), onItemClick = {
                        navController.navigate(it.id)
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
                    LaunchedEffect(Unit) {
                        MobileAds.initialize(context)
                        loadInterstitialAd(context)
                        mainVm.screenEventAction.collect { event ->
                            when (event) {
                                is ScreenEvents.Navigate -> navController.navigate(event.route)
                                is ScreenEvents.SnackAction -> scaffoldState.snackbarHostState.showSnackbar(event.action.getSnackText(context))
                                else -> Timber.e("No implementation for action $event")
                            }
                        }
                    }
                    NavGraph(navController, purchaseHelper)
                    DefaultSnackbar(
                        snackbarHostState = scaffoldState.snackbarHostState,
                        onDismiss = { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenMain() {
    val mainVm = LocalView.current.findViewTreeViewModelStoreOwner().let { hiltViewModel<MainViewModel>(it!!) }
    LaunchedEffect(Unit) {
        mainVm.loadMatchIfSaved()
        mainVm.onEmit(ScreenEvents.Navigate(Screen.Rules.route))
    }
}