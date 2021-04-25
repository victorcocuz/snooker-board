package com.example.snookerscore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.utils.Event

class GenericEventsViewModel: ViewModel() {

    // Observables
    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed

    // Event handlers
    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onMatchActionQueried(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

    fun eventMatchActionConfirmed(matchAction: MatchAction) {
        _eventMatchActionConfirmed.value = Event(matchAction)
    }
}