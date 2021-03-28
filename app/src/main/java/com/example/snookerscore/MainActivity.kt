package com.example.snookerscore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.snookerscore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.rankingsFragment,
                R.id.friendsFragment,
                R.id.playFragment,
                R.id.historyFragment,
                R.id.statisticsFragment
            )
        )
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.apply {
            navBottom.setupWithNavController(navHostFragment.navController)
            setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        }
    }
}