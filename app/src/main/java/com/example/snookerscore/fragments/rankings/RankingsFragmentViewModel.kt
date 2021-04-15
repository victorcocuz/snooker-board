package com.example.snookerscore.fragments.rankings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.repository.SnookerRepository
import kotlinx.coroutines.launch

class RankingsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)

    init {
        viewModelScope.launch {
            snookerRepository.refreshRankings()
        }
    }

    val rankings = snookerRepository.rankings // Live Data

}