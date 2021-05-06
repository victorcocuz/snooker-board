package com.example.snookerscore.fragments.gamestatistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.domain.DomainPlayerScore
import com.example.snookerscore.repository.SnookerRepository
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