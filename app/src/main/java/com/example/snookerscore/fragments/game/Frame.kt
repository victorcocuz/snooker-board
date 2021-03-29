package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData

class Frame {
    private var currentPlayer: CurrentPlayer = CurrentPlayer.PlayerA
    var frameState: FrameState = FrameState.RED
    var playerA = Player()
        private set
    var playerB = Player()
        private set
    var remainingBalls = MutableLiveData(21)
        private set

    fun switchPlayer() {
        currentPlayer = currentPlayer.switchPlayer()
    }

    fun addScore(points: Int) {
        if (frameState != FrameState.COLOR) removeBall()
        frameState = if (remainingBalls.value!! > 6) frameState.alternate() else frameState.nextState()
        when (currentPlayer) {
            CurrentPlayer.PlayerA -> playerA.frameScore.value = playerA.frameScore.value?.plus(points)
            CurrentPlayer.PlayerB -> playerB.frameScore.value = playerB.frameScore.value?.plus(points)
        }
    }

    fun removeBall() {
        remainingBalls.value = remainingBalls.value?.minus(1)
    }

    fun resetFrame() {
        remainingBalls.value = 21
        playerA.frameScore.value = 0
        playerB.frameScore.value = 0
    }
}

class Match {
    var matchScore: Int = 0
}