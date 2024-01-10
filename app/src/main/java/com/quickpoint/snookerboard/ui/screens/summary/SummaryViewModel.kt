package com.quickpoint.snookerboard.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.models.DomainScore
import com.quickpoint.snookerboard.domain.models.emptyDomainScore
import com.quickpoint.snookerboard.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    val score: Flow<ArrayList<Pair<DomainScore, DomainScore>>> = gameRepository.score

    private val _totalsA = MutableStateFlow(emptyDomainScore)
    val totalsA = _totalsA.asStateFlow()
    private val _totalsB = MutableStateFlow(emptyDomainScore)
    val totalsB = _totalsB.asStateFlow()

    init {
        viewModelScope.launch {
            _totalsA.emit(gameRepository.getTotals(0))
            _totalsB.emit(gameRepository.getTotals(1))
        }
    }
}