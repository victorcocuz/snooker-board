package com.quickpoint.snookerboard.fragments.postgame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import kotlinx.coroutines.launch

class PostGameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(application) {

    private val _totalsA = MutableLiveData<DomainScore>()
    val totalsA: LiveData<DomainScore> = _totalsA

    private val _totalsB = MutableLiveData<DomainScore>()
    val totalsB: LiveData<DomainScore> = _totalsB

    val score = snookerRepository.score

    init { // Gets the score from repository and stores it in live data within the vm
        viewModelScope.launch {
            _totalsA.postValue(snookerRepository.getTotals(0))
            _totalsB.postValue(snookerRepository.getTotals(1))
        }
    }
}