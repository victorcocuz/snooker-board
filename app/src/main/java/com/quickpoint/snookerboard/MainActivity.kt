package com.quickpoint.snookerboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.databinding.ActivityMainBinding
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.getSharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityMainBinding
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var sharedPref: SharedPreferences
    // private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        // Create activity, bind layout
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Generate the Game View Model from the get go; pass in the application and an instance of the snookerRepository
        matchViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory(this.application, this, null)
        ).get(MatchViewModel::class.java)
        sharedPref = getSharedPref()

        // To be used when more fragments are needed
        //        binding.apply {
        //            navBottom.setupWithNavController(navController)

        // Hide bottom navigation when not needed
        //            navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
        //                navBottom.visibility = View.GONE
        //                navBottom.visibility = when (nd.id) {
        //                    in listOf(R.id.rankingsFragment, R.id.friendsFragment, R.id.playFragment, R.id.historyFragment, R.id.statisticsFragment) -> View.VISIBLE
        //                    else -> View.GONE
        //                }
        //            }
        //        }
    }

    // When saving instance, check if the view model is initialised (game has started) and if the match is in progress (game hasn't ended). If so, save match
    override fun onSaveInstanceState(outState: Bundle) {
        activityScope.launch {
            if (::matchViewModel.isInitialized && sharedPref.getBoolean(getString(R.string.sp_match_is_in_progress), false)) {
                matchViewModel.saveMatch()
            }
        }
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