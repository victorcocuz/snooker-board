package com.quickpoint.snookerboard.fragments.rankings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.repository.SnookerRepository

class RankingsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)

//    init {
//        viewModelScope.launch {
//            snookerRepository.refreshRankings()
//        }
//    }

//    val rankings = snookerRepository.rankings // Live Data

}