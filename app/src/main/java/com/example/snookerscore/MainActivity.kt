package com.example.snookerscore

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.databinding.ActivityMainBinding
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.getSharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var sharedPref: SharedPreferences
    //    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        //        supportActionBar?.setDisplayShowTitleEnabled(false)

        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(this.application))
        gameViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory(this.application, snookerRepository, this, null)
        ).get(GameViewModel::class.java)
        sharedPref = getSharedPref()


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

    override fun onSaveInstanceState(outState: Bundle) {
        activityScope.launch {
            if (::gameViewModel.isInitialized) gameViewModel.saveMatch()
        }
        super.onSaveInstanceState(outState)
    }
}