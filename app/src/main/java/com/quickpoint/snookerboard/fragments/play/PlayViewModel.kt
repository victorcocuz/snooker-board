package com.quickpoint.snookerboard.fragments.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.DomainMatchInfo
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*

class PlayViewModel: ViewModel() {
    // Live Data
    private val _eventRules = MutableLiveData(RULES)
    val eventRules: LiveData<RULES> = _eventRules
    fun onUpdateRules(rules: RULES) {
        _eventRules.value = rules
    }
}