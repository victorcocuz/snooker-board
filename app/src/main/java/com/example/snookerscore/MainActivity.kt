package com.example.snookerscore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.snookerscore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val setOfPrimaryFragments = setOf(R.id.rankingsFragment, R.id.friendsFragment, R.id.playFragment, R.id.historyFragment, R.id.statisticsFragment)
    private val appBarConfiguration = AppBarConfiguration(setOfPrimaryFragments)
    private val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            navBottom.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)

            // Hide bottom navigation when not needed
            navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
                navBottom.visibility = when (nd.id) {
                    in setOfPrimaryFragments -> View.VISIBLE
                    else -> View.GONE
                }
            }
        }
    }
}