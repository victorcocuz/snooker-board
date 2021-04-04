package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.Event

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val app = application

    val shotType = MutableLiveData<ShotType>()
    val isFoulDialogOpen = MutableLiveData(false)

    // Dialog stuff
    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    fun onMiss() {
        frame.onMiss()
        shotType.value = ShotType.MISS
    }

    fun onFoul() {
        _eventFoul.value = Event(Unit)
        shotType.value = ShotType.FOUL
    }

    fun onBallClicked(ball: Ball, shotType: ShotType) {
        frame.addScore(ball, shotType)
    }

    fun onFrameComplete() {
        frame.resetFrame()
    }

    fun onUndo() {
        frame.undo()
    }
}