package com.quickpoint.snookerboard.ui.fragments.summary

import androidx.lifecycle.*
import com.quickpoint.snookerboard.domain.DomainScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.base.Event
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val snookerRepository: SnookerRepository,
) : ViewModel() {

    private val _eventSummaryAction = MutableLiveData<Event<MatchAction?>>()
    val eventSummaryAction: LiveData<Event<MatchAction?>> = _eventSummaryAction
    fun onEventSummaryAction(matchAction: MatchAction?) {
        _eventSummaryAction.value = Event(matchAction)
    }

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