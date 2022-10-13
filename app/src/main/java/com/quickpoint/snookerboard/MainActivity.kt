package com.quickpoint.snookerboard

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.quickpoint.snookerboard.databinding.ActivityMainBinding
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.MatchState.*
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.getSharedPref
import com.quickpoint.snookerboard.utils.hideKeyboard
import com.quickpoint.snookerboard.utils.savePref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityMainBinding
    private lateinit var matchVm: MatchViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        // Create activity, bind layout
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        hideKeyboard()

        // Generate the matchVm from the get to be readily accessed from all fragments when needed, pass in application and repository
        matchVm = ViewModelProvider(
            this,
            GenericViewModelFactory(application, this, null)
        )[MatchViewModel::class.java]

        // Condition navigation entry point depending if there is a game in progress or not
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        navGraph.setStartDestination(
            when (RULES.state) {
                IDLE -> R.id.playFragment
                IN_PROGRESS -> R.id.gameFragment
                POST_MATCH -> R.id.gameStatsFragment
            }
        )
        navController.graph = navGraph


        // To be used when more fragments are needed
        //        binding.apply {
        //            navBottom.setupWithNavController(navController)
        //            navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
        //                navBottom.visibility = View.GONE
        //                navBottom.visibility = when (nd.id) {
        //                    in listOf(R.id.rankingsFragment, R.id.friendsFragment, R.id.playFragment, R.id.historyFragment, R.id.statisticsFragment) -> View.VISIBLE
        //                    else -> View.GONE
        //                }
        //            }
        //        }
    }

    // When saving instance, check if the view model is initialised and if the match is in progress (game hasn't ended). If so, save match
    override fun onSaveInstanceState(outState: Bundle) {
        getSharedPref().savePref(application)
        matchVm.saveMatchOnSavedInstance()
        super.onSaveInstanceState(outState)
    }

    // Remove focus from edit text on outside click
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
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