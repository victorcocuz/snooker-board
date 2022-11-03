package com.quickpoint.snookerboard.fragments.postgame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.DomainPlayerScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import kotlinx.coroutines.launch

class PostGameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(application) {

    private val _totalsA = MutableLiveData<DomainPlayerScore>()
    val totalsA: LiveData<DomainPlayerScore> = _totalsA

    private val _totalsB = MutableLiveData<DomainPlayerScore>()
    val totalsB: LiveData<DomainPlayerScore> = _totalsB

    val score = snookerRepository.score

    init { // Gets the score from repository and stores it in live data within the vm
        viewModelScope.launch {
            _totalsA.postValue(snookerRepository.getTotals(0))
            _totalsB.postValue(snookerRepository.getTotals(1))
        }
    }
}