package com.quickpoint.snookerboard.utils

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.SnookerBoardApplication
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.fragments.summary.SummaryViewModel

class GenericViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(RulesViewModel::class.java)) {
            return RulesViewModel() as T
        }
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            return SummaryViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
        }
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
        }
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            return MatchViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}