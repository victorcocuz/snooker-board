package com.example.snookerscore.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var frame: Frame = Frame()
    private val _frameState = frame.frameState
    val frameState: LiveData<BallType>
        get() = _frameState
    val pointsDiff = frame.pointsDiff
    val pointsLeft = frame.pointsRemaining

    fun onScored(ball: Ball) {
        frame.addScore(ball)
    }

    fun onMiss() {
        frame.switchPlayer()
    }

    fun onFrameComplete() {
        frame.resetFrame()
    }

    fun onUndo() {
        frame.undo()
    }
}