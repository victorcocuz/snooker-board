package com.quickpoint.snookerboard

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.MobileAds
import com.quickpoint.snookerboard.databinding.ActivityMainBinding
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.utils.MatchState.GAME_SAVED
import com.quickpoint.snookerboard.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.utils.MatchState.RULES_PENDING
import com.quickpoint.snookerboard.utils.removeFocusAndHideKeyboard
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainVm: MainViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Keep splash screen on until match loading check is complete
        splashScreen.setKeepOnScreenCondition { mainVm.keepSplashScreen.value }
        super.onCreate(savedInstanceState) // Create view after installing splash screen

        // Initiate mainVm from the start to be readily accessed from all fragments when needed; pass in application and repository
        mainVm = ViewModelProvider(this, GenericViewModelFactory(this, null))[MainViewModel::class.java]

        // Bind view elements
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            // Set Appbar
            navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            setSupportActionBar(layoutAppBarMain.lToolbarMain)
            NavigationUI.setupActionBarWithNavController(this@MainActivity, navController, mainDrawerLayout)
            appBarConfiguration = AppBarConfiguration(navController.graph, mainDrawerLayout)
            NavigationUI.setupWithNavController(mainActivityNavView, navController)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            // Prevent nav gesture if not on start destination
            navController.addOnDestinationChangedListener { _: NavController, nd: NavDestination, _: Bundle? ->
                when (nd.id) {
                    R.id.RulesFragment -> mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    R.id.navRulesFragment, R.id.navImproveFragment, R.id.navDonateFragment, R.id.navSettingsFragment, R.id.navAboutFragment -> {
                        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                    else -> {
                        binding.layoutAppBarMain.lToolbarMain.navigationIcon = null
                        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                }
            }
        }

        // AdMob
        MobileAds.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        mainVm.loadMatchIfSaved()
    }

    override fun onSaveInstanceState(outState: Bundle) { // Save state and shared preferences on pause rather than onSaveInstanceState so that db save can complete
        mainVm.updateState(when (SETTINGS.matchState) {
            RULES_IDLE -> RULES_PENDING
            GAME_IN_PROGRESS -> GAME_SAVED
            else -> SETTINGS.matchState
        })
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