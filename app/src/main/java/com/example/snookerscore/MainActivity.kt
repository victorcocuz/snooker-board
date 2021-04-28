package com.example.snookerscore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.databinding.ActivityMainBinding
import com.example.snookerscore.domain.CurrentScore
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.repository.SnookerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityMainBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        supportActionBar?.setDisplayShowTitleEnabled(false)

        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(this.application))
        gameViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory(this.application, snookerRepository, this, null)
        ).get(GameViewModel::class.java)

        binding.apply {
            navBottom.setupWithNavController(navController)

            // Hide bottom navigation when not needed
            navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
                navBottom.visibility = View.GONE
//                navBottom.visibility = when (nd.id) {
//                    in listOf(R.id.rankingsFragment, R.id.friendsFragment, R.id.playFragment, R.id.historyFragment, R.id.statisticsFragment) -> View.VISIBLE
//                    else -> View.GONE
//                }
            }
        }

        // VM Observers
        snookerRepository.apply {
            currentScore.observe(this@MainActivity, {
                if (it is CurrentScore) {
                    gameViewModel.setScore(it)
                }
            })
            currentBreaks.observe(this@MainActivity, {
                if (it.size > 0) {
                    gameViewModel.setFrameStack(it)
                }
            })
            currentBallStack.observe(this@MainActivity, {
                if (it.size > 0) {
                    gameViewModel.setBallStack(it)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        activityScope.launch {
            if (gameViewModel.displayScore.value!!.isMatchInProgress()) {
                snookerRepository.deleteCurrentMatch()
                snookerRepository.saveCurrentMatch(
                    gameViewModel.displayScore.value!!,
                    gameViewModel.displayFrameStack.value!!,
                    gameViewModel.displayBallStack.value!!
                )
                if (::gameViewModel.isInitialized) {
                    gameViewModel.setSavedStateRules()
                }
            }
        }
        super.onSaveInstanceState(outState)
    }
}