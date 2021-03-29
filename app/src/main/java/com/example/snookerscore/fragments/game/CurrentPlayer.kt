package com.example.snookerscore.fragments.game

sealed class CurrentPlayer {

    object PlayerA : CurrentPlayer()
    object PlayerB : CurrentPlayer()

    fun switchPlayer() : CurrentPlayer = when (this) {
        PlayerA -> PlayerB
        PlayerB -> PlayerA
    }
}

sealed class FrameState {
    object RED: FrameState()
    object COLOR: FrameState()
    object YELLOW: FrameState()
    object GREEN: FrameState()
    object BROWN: FrameState()
    object BLUE: FrameState()
    object PINK: FrameState()
    object BLACK: FrameState()
    object END: FrameState()

    fun nextState() : FrameState = when (this) {
        RED -> COLOR
        COLOR -> YELLOW
        YELLOW -> GREEN
        GREEN -> BROWN
        BROWN -> BLUE
        BLUE -> PINK
        PINK -> BLACK
        BLACK -> END
        END -> RED
    }

    fun alternate() : FrameState = when (this) {
        RED -> COLOR
        else -> RED
    }
}