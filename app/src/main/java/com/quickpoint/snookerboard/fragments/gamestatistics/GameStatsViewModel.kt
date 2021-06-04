package com.quickpoint.snookerboard.fragments.gamestatistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.domain.DomainPlayerScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import kotlinx.coroutines.launch

class GameStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)

    private val _totalsA = MutableLiveData<DomainPlayerScore>()
    val totalsA: MutableLiveData<DomainPlayerScore> = _totalsA

    private val _totalsB = MutableLiveData<DomainPlayerScore>()
    val totalsB: MutableLiveData<DomainPlayerScore> = _totalsB

    val score = snookerRepository.score

    fun getTotals() = viewModelScope.launch {
        _totalsA.postValue(snookerRepository.getTotals(0))
        _totalsB.postValue(snookerRepository.getTotals(1))
    }
}