package com.quickpoint.snookerboard.ui.fragments.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.emptyDomainScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.MatchAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val snookerRepository: SnookerRepository,
) : ViewModel() {

    private val _eventSummaryAction = MutableLiveData<Event<MatchAction?>>()
    val eventSummaryAction: LiveData<Event<MatchAction?>> = _eventSummaryAction
    fun onEventSummaryAction(matchAction: MatchAction?) {
        _eventSummaryAction.value = Event(matchAction)
    }

    private val _totalsA = MutableStateFlow(emptyDomainScore)
    val totalsA = _totalsA.asStateFlow()

    private val _totalsB = MutableStateFlow(emptyDomainScore)
    val totalsB = _totalsB.asStateFlow()

    val score = snookerRepository.score

    init { // Gets the score from repository and stores it in live data within the vm
        viewModelScope.launch {
            _totalsA.emit(snookerRepository.getTotals(0))
            _totalsB.emit(snookerRepository.getTotals(1))
        }
    }
}