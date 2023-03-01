package com.quickpoint.snookerboard.ui.fragments.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.emptyDomainScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val snookerRepository: SnookerRepository,
) : ViewModel() {

    val score = snookerRepository.score

    private val _totalsA = MutableStateFlow(emptyDomainScore)
    val totalsA = _totalsA.asStateFlow()
    private val _totalsB = MutableStateFlow(emptyDomainScore)
    val totalsB = _totalsB.asStateFlow()

    init {
        viewModelScope.launch {
            _totalsA.emit(snookerRepository.getTotals(0))
            _totalsB.emit(snookerRepository.getTotals(1))
        }
    }
}