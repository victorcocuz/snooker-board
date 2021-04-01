package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var frame: Frame = Frame()
    private val _frameState = MutableLiveData(frame.frameState)
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsLeft

    fun onScored(ball: Ball) {
        frame.addScore(ball)
        _frameState.value = frame.frameState
    }

    fun onMiss() {
        frame.switchPlayer()
    }

    fun onFrameComplete() {
        frame.resetFrame()
    }
}