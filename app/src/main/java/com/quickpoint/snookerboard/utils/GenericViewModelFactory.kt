package com.quickpoint.snookerboard.utils

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.quickpoint.snookerboard.SnookerApplication
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.fragments.gamestatistics.GameStatsViewModel
import com.quickpoint.snookerboard.fragments.play.PlayViewModel
import com.quickpoint.snookerboard.fragments.rankings.RankingsFragmentViewModel

class GenericViewModelFactory(
    private val application: Application,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(RankingsFragmentViewModel::class.java)) {
            return RankingsFragmentViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(PlayViewModel::class.java)) {
            return PlayViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(GameStatsViewModel::class.java)) {
            return GameStatsViewModel(application, SnookerApplication.getSnookerRepository()) as T
        }
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel() as T
        }
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            return MatchViewModel(application, SnookerApplication.getSnookerRepository(), application.getSharedPref()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}