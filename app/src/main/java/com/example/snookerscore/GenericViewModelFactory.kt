package com.example.snookerscore

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.fragments.gamestatistics.GameStatsViewModel
import com.example.snookerscore.fragments.rankings.RankingsFragmentViewModel
import com.example.snookerscore.repository.SnookerRepository

class GenericViewModelFactory(
    private val application: Application,
    private val snookerRepository: SnookerRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(RankingsFragmentViewModel::class.java)) {
            return RankingsFragmentViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(GameStatsViewModel::class.java)) {
            return GameStatsViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(application, snookerRepository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}