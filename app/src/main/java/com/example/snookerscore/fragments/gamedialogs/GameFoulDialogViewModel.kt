package com.example.snookerscore.fragments.gamedialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.domain.DomainBall
import com.example.snookerscore.domain.DomainPot
import com.example.snookerscore.domain.PotAction
import com.example.snookerscore.utils.Event

class GameFoulDialogViewModel : ViewModel() {
    // Observables
    private val _eventFoulNotValid = MutableLiveData<Event<Unit>>()
    val eventFoulNotValid: LiveData<Event<Unit>> = _eventFoulNotValid

    private val _foulConfirmed = MutableLiveData<Event<DomainPot>>()
    val foul: LiveData<Event<DomainPot>> = _foulConfirmed

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked

    private val _freeBall = MutableLiveData(false)
    val freeBall: LiveData<Boolean> = _freeBall

    private val _removeRed = MutableLiveData(false)
    val removeRed: LiveData<Boolean> = _removeRed

    // Variables
    private var ballClicked: DomainBall? = null

    // Handlers
    fun onBallClicked(ball: DomainBall) {
        ballClicked = ball
    }

    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
        if (action == PotAction.CONTINUE) _freeBall.value = false
    }

    fun onRemoveRedClicked() {
        _removeRed.value = !_removeRed.value!!
    }

    fun onFreeballClicked() {
        _freeBall.value = !_freeBall.value!!
    }

    fun onConfirmClicked() {
        if (ballClicked != null && actionClicked.value != null) {
            _foulConfirmed.value = Event(DomainPot.FOUL(ballClicked!!, actionClicked.value!!))
        } else {
            _eventFoulNotValid.value = Event(Unit)
        }
    }
}