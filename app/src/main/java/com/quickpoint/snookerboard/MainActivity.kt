package com.quickpoint.snookerboard

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.databinding.ActivityMainBinding
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.MatchState.IN_PROGRESS
import com.quickpoint.snookerboard.domain.MatchState.SAVED
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.getSharedPref
import com.quickpoint.snookerboard.utils.loadPref
import com.quickpoint.snookerboard.utils.savePref
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var matchVm: MatchViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initiate the matchVm from the start to be readily accessed from all fragments when needed; pass in application and repository
        matchVm = ViewModelProvider(this, GenericViewModelFactory( this, null))[MatchViewModel::class.java]

        // Load existing match, keep splash screen on until loading is complete
        val splashScreen = installSplashScreen()
        var keep = true
        splashScreen.setKeepOnScreenCondition { keep }
        matchVm.keepSplashScreen.observe(this) { keep = it }

        // Bind view elements
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            // Set Appbar
            val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            setSupportActionBar(layoutAppBarMain.layoutToolbarMain)
            NavigationUI.setupActionBarWithNavController(this@MainActivity, navController, mainDrawerLayout)
            appBarConfiguration = AppBarConfiguration(navController.graph, mainDrawerLayout)
            NavigationUI.setupWithNavController(mainActivityNavView, navController)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            // Prevent nav gesture if not on start destination
            navController.addOnDestinationChangedListener { _: NavController, nd: NavDestination, _: Bundle? ->
                when (nd.id) {
                    R.id.playFragment -> mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    R.id.navRulesFragment, R.id.navImproveFragment, R.id.navAboutFragment -> mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    else -> {
                        binding.layoutAppBarMain.layoutToolbarMain.navigationIcon = null
                        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                }
            }
        }
    }

    override fun onStart() { // Load shared preferences
        getSharedPref().loadPref(application)
        matchVm.updateRules(-1)
        super.onStart()
    }

    override fun onResume() { // When match state is saved, if db is empty reset rules, otherwise load the most recent frame
        matchVm.dbCrtFrame.observe(this) { crtFrame ->
            Timber.i("CrtFrame is ${crtFrame?.frame?.frameId}")
            if (RULES.matchState == SAVED && crtFrame == null) {
                matchVm.turnOffSplashScreen()
                RULES.resetRules()
            }
            if (RULES.matchState == SAVED && crtFrame?.frame?.frameId == RULES.frameCount) {
                matchVm.updateFrameInfo(crtFrame.asDomainFrame())
                matchVm.deleteCrtFrameFromDb()
                RULES.setMatchState(IN_PROGRESS)
            }
            matchVm.updateRules(-1)
        }
        super.onResume()
    }

    override fun onPause() { // Save state and shared preferences on pause rather than onSaveInstanceState so that db save can complete
        if (RULES.matchState == IN_PROGRESS) {
            if (::matchVm.isInitialized) {
                matchVm.saveMatchOnSavedInstance()
                RULES.setMatchState(SAVED)
                getSharedPref().savePref(application)
            } else Timber.e("matchVm is not initialised")
        }
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean { // Add back arrow button to navigation
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean { // Remove focus from edit text on outside click
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}