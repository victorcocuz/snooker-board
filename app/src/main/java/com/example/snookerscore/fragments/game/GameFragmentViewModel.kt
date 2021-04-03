package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsRemaining
    val app = application

    val shotType = MutableLiveData<ShotType>()
    val isFoulDialogOpen = MutableLiveData(false)

    // Dialog stuff
    val foulCheck = MutableLiveData(false)

    fun onMiss() {
        frame.onMiss()
        shotType.value = ShotType.MISS
    }

    fun onFoul() {
        foulCheck.value = true
        shotType.value = ShotType.FOUL
    }

    fun onBallClicked(ball: Ball) {
        frame.addScore(ball)
    }

    fun onFrameComplete() {
        frame.resetFrame()
    }

    fun onUndo() {
        frame.undo()
    }
}