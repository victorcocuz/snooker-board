package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.Event
import java.util.*

class GameFragmentViewModel(
    application: Application,
    val matchFrames: Int,
    private val matchReds: Int,
    private val matchFoulModifier: Int,
    private val matchBreaksFirst: Int
) :
    AndroidViewModel(application) {

    // Frame Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _displayPlayer = MutableLiveData<CurrentPlayer>()
    val displayPlayer: LiveData<CurrentPlayer> = _displayPlayer

    private val _ballStackSize = MutableLiveData<Int>()
    val ballStackSize: LiveData<Int> = _ballStackSize

    private val _frameStackSize = MutableLiveData<Int>()
    val frameStackSize: LiveData<Int> = _frameStackSize

    // Match Observables
    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    // Variables
    private lateinit var crtPlayer: CurrentPlayer
    private var ballStack = ArrayDeque<Ball>()
    private var frameStack = ArrayDeque<Shot>()

    // Init
    init {
        resetMatch()
    }

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(crtPlayer, pot.ball, pot.potType, pot.potAction)
        if (removeRed) updateFrame(crtPlayer, (if (inColors()) ballStack.peek() else Balls.RED), PotType.REMOVE_RED, PotAction.Continue)
        if (freeBall) {
            if (inColors()) ballStack.push(Balls.FREE) else for (ball in listOf(Balls.COLOR, Balls.FREE)) ballStack.push(ball)
            getFrameStatus()
        }
    }

    fun onBallClicked(pot: Pot) = updateFrame(crtPlayer, pot.ball, pot.potType, PotAction.Continue)

    fun onSafeClicked() = updateFrame(crtPlayer, Balls.NOBALL, PotType.SAFE, PotAction.Switch)

    fun onMissClicked() = updateFrame(crtPlayer, Balls.NOBALL, PotType.MISS, PotAction.Switch)

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() = updateFrame(crtPlayer, Balls.RED, PotType.ADD_RED, PotAction.Continue)

    fun onRerackClicked() = resetFrame()

    fun onUndoClicked() {
        val lastShot = frameStack.pop()
        crtPlayer = lastShot.player
        when (lastShot.pot.potType) {
            PotType.HIT -> ballStack.push(if (isColor()) Balls.COLOR else lastShot.pot.ball)
            PotType.ADD_RED -> for (ball in listOf(Balls.RED, Balls.COLOR)) ballStack.push(ball)
            PotType.FOUL -> {
                if (ballStack.peek()!!.ballType == BallType.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (isColor()) ballStack.push(Balls.COLOR)
            }
            PotType.REMOVE_RED -> {
                onUndoClicked()
                for (ball in when (isColor()) {
                    true -> listOf(Balls.COLOR, Balls.RED)
                    false -> listOf(Balls.RED, Balls.COLOR)
                }) ballStack.push(ball)
            }
            else -> {
            }
        }
        calcPoints(crtPlayer, lastShot.pot.ball, lastShot.pot.potType, -1)
        getFrameStatus()
    }

    // Frame Helpers
    private fun updateFrame(currentPlayer: CurrentPlayer, ball: Ball, potType: PotType, potAction: PotAction) {
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE) -> ballStack.pop()
            in listOf(PotType.REMOVE_RED, PotType.ADD_RED) -> repeat(2) { ballStack.pop() }
            else -> {
                if (potAction == PotAction.Switch) crtPlayer = crtPlayer.otherPlayer() // Switch players
                if (ballStack.peek() == Balls.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (ballStack.peek() == Balls.COLOR) ballStack.pop()
            }
        }
        calcPoints(currentPlayer, ball, potType, 1)
        if (ballStack.size == 1 && crtPlayer.framePoints == crtPlayer.otherPlayer().framePoints) ballStack.push(Balls.BLACK)
        frameStack.push(Shot(currentPlayer, _frameState.value!!, Pot(ball, potType, potAction)))
        getFrameStatus()
    }

    private fun calcPoints(crtPlayer: CurrentPlayer, ball: Ball, potType: PotType, pol: Int) {
        val points = when (potType) { // foul from sinking the white should equal min ball foul on the table
            PotType.FOUL ->
                if (ballStack.size in 1..4 && ball == Balls.WHITE) ballStack.peek()!!.foulPoints + matchFoulModifier
                else ball.foulPoints + matchFoulModifier
            PotType.REMOVE_RED -> 0
            PotType.FREE -> if (inColors()) 1 else ballStack.peek()!!.points
            else -> ball.points
        }
        if (potType == PotType.FOUL) crtPlayer.otherPlayer().addFramePoints(pol * points)
        else crtPlayer.addFramePoints(pol * points)
    }

    private fun getFrameStatus() {
        if (ballStack.size == 1) endFrame()
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size
        _frameStackSize.value = frameStack.size
        _displayPlayer.value = crtPlayer
    }

    private fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

    private fun inColors(): Boolean = ballStack.size <= 7
    private fun isColor(): Boolean = ballStack.size in (7..35).filter { it % 2 != 0 }

    // Match Handler Functions
    fun onCancelClicked() {
        _eventCancelDialog.value = Event(Unit)
    }

    fun onCancelMatchClicked() = assignMatchAction(MatchAction.CANCEL_MATCH)

    fun onEndFrameClicked() = endFrame()

    fun onEndMatchClicked() = assignMatchAction(MatchAction.END_MATCH)

    fun onGenDialogConfirmed(matchAction: MatchAction) {
        _eventMatchActionConfirmed.value = Event(matchAction)
    }

    // Match Helpers
    fun matchEnded() {
        frameEnded()
        resetMatch()
    }

    fun frameEnded() {
        if (crtPlayer.getFirst().framePoints > crtPlayer.getSecond().framePoints) crtPlayer.getFirst().incrementMatchPoint()
        else crtPlayer.getSecond().incrementMatchPoint()
        resetFrame()
    }

    private fun endFrame() {
        val player =
            if (crtPlayer.getFirst().framePoints > crtPlayer.getSecond().framePoints) crtPlayer.getFirst()
            else crtPlayer.getSecond()
        if (player.matchPoints + 1 == matchFrames) assignMatchAction(MatchAction.MATCH_ENDED)
        else assignMatchAction(if (ballStack.size == 1) MatchAction.FRAME_ENDED else MatchAction.END_FRAME)
    }

    fun resetMatch() {
        crtPlayer = if (matchBreaksFirst == 0) CurrentPlayer.PlayerA else CurrentPlayer.PlayerB
        crtPlayer.getFirst().matchPoints = 0
        crtPlayer.getSecond().matchPoints = 0
        resetFrame()
    }

    private fun resetFrame() {
        if (crtPlayer.getFirst().matchPoints != 0 || crtPlayer.getSecond().matchPoints != 0) crtPlayer = crtPlayer.otherPlayer()
        crtPlayer.getFirst().framePoints = 0
        crtPlayer.getSecond().framePoints = 0
        ballStack.apply {
            frameStack.clear()
            clear()
            for (ball in listOf(Balls.NOBALL, Balls.BLACK, Balls.PINK, Balls.BLUE, Balls.BROWN, Balls.GREEN, Balls.YELLOW)) push(ball)
            repeat(matchReds) { for (ball in listOf(Balls.COLOR, Balls.RED)) push(ball) }
        }
        getFrameStatus()
    }
}