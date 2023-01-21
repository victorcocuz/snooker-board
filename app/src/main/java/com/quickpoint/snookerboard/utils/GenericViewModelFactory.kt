package com.quickpoint.snookerboard.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.SnookerBoardApplication
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.fragments.summary.SummaryViewModel

//class GenericViewModelFactory(
//    owner: SavedStateRegistryOwner,
//    defaultArgs: Bundle?
//) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
//        if (modelClass.isAssignableFrom(RulesViewModel::class.java)) {
//            return RulesViewModel() as T
//        }
//        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
//            return SummaryViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
//        }
//        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
//            return GameViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
//        }
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return MainViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")    }
//}

class GenericViewModelFactory(private val dataStore: DataStore) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RulesViewModel::class.java)) {
            return RulesViewModel(SnookerBoardApplication.application()) as T
        }
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            return SummaryViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
        }
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository()) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(SnookerBoardApplication.application(), SnookerBoardApplication.getSnookerRepository(), dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}