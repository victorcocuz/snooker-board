package com.example.snookerscore.fragments.gamedialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.Pot
import com.example.snookerscore.fragments.game.PotAction
import com.example.snookerscore.fragments.game.PotType
import com.example.snookerscore.utils.Event

class GameFoulDialogViewModel : ViewModel() {
    // Observables
    private val _eventFoulNotValid = MutableLiveData<Event<Unit>>()
    val eventFoulNotValid: LiveData<Event<Unit>> = _eventFoulNotValid

    private val _foulConfirmed = MutableLiveData<Event<Pot>>()
    val foul: LiveData<Event<Pot>> = _foulConfirmed

    private val _actionClicked = MutableLiveData<PotAction?>(null)
    val actionClicked: LiveData<PotAction?> = _actionClicked

    private val _freeBall = MutableLiveData(false)
    val freeBall: LiveData<Boolean> = _freeBall

    private val _removeRed = MutableLiveData(false)
    val removeRed: LiveData<Boolean> = _removeRed

    // Variables
    private var ballClicked: Ball? = null

    // Handlers
    fun onBallClicked(ball: Ball) {
        ballClicked = ball
    }

    fun onActionClicked(action: PotAction) {
        _actionClicked.value = action
        if (action == PotAction.Continue) _freeBall.value = false
    }

    fun onRemoveRedClicked() {
        _removeRed.value = !_removeRed.value!!
    }

    fun onFreeballClicked() {
        _freeBall.value = !_freeBall.value!!
    }

    fun onConfirmClicked() {
        if (ballClicked != null && actionClicked.value != null) {
            _foulConfirmed.value = Event(Pot(ballClicked!!, PotType.FOUL, actionClicked.value!!))
        } else {
            _eventFoulNotValid.value = Event(Unit)
        }
    }
}