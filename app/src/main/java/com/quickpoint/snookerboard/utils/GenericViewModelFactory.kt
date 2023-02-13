package com.quickpoint.snookerboard.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.SnookerApp
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.ui.fragments.navdrawer.DrawersViewModel
import com.quickpoint.snookerboard.ui.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.ui.fragments.summary.SummaryViewModel

class GenericViewModelFactory(
    private val dataStore: DataStore? = null,
    private val navController: NavHostController? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(SnookerApp.repository()) as T
        }
        if (modelClass.isAssignableFrom(RulesViewModel::class.java)) {
            return RulesViewModel() as T
        }
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            return SummaryViewModel(SnookerApp.repository()) as T
        }
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(SnookerApp.repository()) as T
        }
        if (modelClass.isAssignableFrom(DrawersViewModel::class.java)) {
            return DrawersViewModel(dataStore!!) as T
        }
        if (modelClass.isAssignableFrom(DialogViewModel::class.java)) {
            return DialogViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}