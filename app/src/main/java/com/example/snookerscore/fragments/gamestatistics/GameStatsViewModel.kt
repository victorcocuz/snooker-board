package com.example.snookerscore.fragments.gamestatistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.fragments.game.FrameScore
import com.example.snookerscore.repository.SnookerRepository
import kotlinx.coroutines.launch

class GameStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)

    private val _totalsA = MutableLiveData<FrameScore>()
    val totalsA: MutableLiveData<FrameScore> = _totalsA

    private val _totalsB = MutableLiveData<FrameScore>()
    val totalsB: MutableLiveData<FrameScore> = _totalsB

    val frames = snookerRepository.frames

    fun getTotals() = viewModelScope.launch {
        _totalsA.postValue(snookerRepository.getTotals(0))
        _totalsB.postValue(snookerRepository.getTotals(1))
    }
}