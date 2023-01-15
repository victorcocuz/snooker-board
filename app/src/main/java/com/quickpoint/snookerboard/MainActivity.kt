package com.quickpoint.snookerboard

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.ads.MobileAds
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.compose.navigation.AppBar
import com.quickpoint.snookerboard.compose.navigation.DrawerBody
import com.quickpoint.snookerboard.compose.navigation.DrawerHeader
import com.quickpoint.snookerboard.compose.navigation.NavGraph
import com.quickpoint.snookerboard.compose.navigation.Screen
import com.quickpoint.snookerboard.compose.navigation.getMenuItems
import com.quickpoint.snookerboard.compose.ui.styles.GenericSurface
import com.quickpoint.snookerboard.compose.ui.theme.Green
import com.quickpoint.snookerboard.compose.ui.theme.SnookerBoardTheme
import com.quickpoint.snookerboard.compose.ui.theme.Transparent
import com.quickpoint.snookerboard.databinding.ActivityMainBinding
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.utils.MatchState.GAME_SAVED
import com.quickpoint.snookerboard.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.utils.MatchState.RULES_PENDING
import com.quickpoint.snookerboard.utils.removeFocusAndHideKeyboard
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainVm: MainViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Keep splash screen on until match loading check is complete
//        splashScreen.setKeepOnScreenCondition { mainVm.keepSplashScreen.value }
        super.onCreate(savedInstanceState) // Create view after installing splash screen

////         Initiate mainVm from the start to be readily accessed from all fragments when needed; pass in application and repository
//        mainVm = ViewModelProvider(this, GenericViewModelFactory(this, null))[MainViewModel::class.java]
//
////         Bind view elements
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        binding.apply {
//            // Set Appbar
//            navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
//            setSupportActionBar(layoutAppBarMain.lToolbarMain)
//            NavigationUI.setupActionBarWithNavController(this@MainActivity, navController, mainDrawerLayout)
//            appBarConfiguration = AppBarConfiguration(navController.graph, mainDrawerLayout)
//            NavigationUI.setupWithNavController(mainActivityNavView, navController)
//            supportActionBar?.setDisplayShowTitleEnabled(false)
//
//            // Prevent nav gesture if not on start destination
//            navController.addOnDestinationChangedListener { _: NavController, nd: NavDestination, _: Bundle? ->
//                when (nd.id) {
//                    R.id.RulesFragment -> mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                    R.id.navRulesFragment, R.id.navImproveFragment, R.id.navDonateFragment, R.id.navSettingsFragment, R.id.navAboutFragment -> {
//                        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    }
//                    else -> {
//                        binding.layoutAppBarMain.lToolbarMain.navigationIcon = null
//                        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    }
//                }
//            }
//        }

        // AdMob
        MobileAds.initialize(this)

        setContent {
            SnookerBoardApp(this)
        }
    }

    override fun onStart() {
        super.onStart()
//        mainVm.loadMatchIfSaved()
    }

    override fun onSaveInstanceState(outState: Bundle) { // Save state and shared preferences on pause rather than onSaveInstanceState so that db save can complete
        mainVm.updateState(
            when (SETTINGS.matchState) {
                RULES_IDLE -> RULES_PENDING
                GAME_IN_PROGRESS -> GAME_SAVED
                else -> SETTINGS.matchState
            }
        )
        Timber.i(getString(R.string.helper_save_match))
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean { // Remove focus from edit text on outside click
        currentFocus?.removeFocusAndHideKeyboard(this, event)
        return super.dispatchTouchEvent(event)
    }
}

@Composable
fun SnookerBoardApp(activity: MainActivity) {
    val mainVm: MainViewModel = viewModel(factory = GenericViewModelFactory())
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
                                "id_drawer_rules" -> Screen.DrawerRulesScreen.route
                                "id_drawer_improve" -> Screen.DrawerImproveScreen.route
                                "id_drawer_support" -> Screen.DrawerSupportScreen.route
                                "id_drawer_settings" -> Screen.DrawerSettingsScreen.route
                                "id_drawer_about" -> Screen.DrawerAboutScreen.route
                                else -> Screen.RulesScreen.route // Unused
                            }
                        )
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                    })
                },
                backgroundColor = Transparent,
            drawerBackgroundColor = Green) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NavGraph(navController, mainVm, purchaseHelper)
                    DefaultSnackbar(
                        snackbarHostState = scaffoldState.snackbarHostState,
                        onDismiss = { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    LaunchedEffect(key1 = true) {
                        mainVm.eventSharedFlow.collect { event ->
                            when(event) {
                                is MainViewModel.ScreenEvents.ShowSnackbar -> {
                                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                                }
                                is MainViewModel.ScreenEvents.Navigate -> {
                                    navController.navigate(event.route)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
