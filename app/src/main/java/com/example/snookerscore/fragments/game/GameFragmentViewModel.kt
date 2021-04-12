package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snookerscore.utils.Event
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class GameFragmentViewModel(
    application: Application,
    val matchFrames: Int,
    private val matchReds: Int,
    private val matchFoulModifier: Int,
    private val matchBreaksFirst: Int
) : AndroidViewModel(application) {

    // Frame Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _displayPlayer = MutableLiveData<CurrentFrame>()
    val displayPlayer: LiveData<CurrentFrame> = _displayPlayer

    private val _ballStackSize = MutableLiveData<Int>()
    val ballStackSize: LiveData<Int> = _ballStackSize

    private val _displayFrameStack = MutableLiveData<ArrayDeque<Break>>()
    val displayFrameStack: LiveData<ArrayDeque<Break>> = _displayFrameStack

    // Match Observables
    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    // Variables
    private lateinit var crtMatch: CurrentMatch
    private lateinit var crtFrame: CurrentFrame
    private var ballStack = ArrayDeque<Ball>()
    private val frameStack = ArrayDeque<Break>()

    // Init
    init {
        resetMatch()
    }

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(crtFrame, pot.ball, pot.potType, pot.potAction)
        if (removeRed) updateFrame(crtFrame, (if (inColors()) ballStack.peek() else Balls.RED), PotType.REMOVE_RED, PotAction.Continue)
        if (freeBall) {
            if (inColors()) ballStack.push(Balls.FREE) else for (ball in listOf(Balls.COLOR, Balls.FREE)) ballStack.push(ball)
            getFrameStatus()
        }
    }

    fun onBallClicked(pot: Pot) = updateFrame(crtFrame, pot.ball, pot.potType, PotAction.Continue)

    fun onSafeClicked() = updateFrame(crtFrame, Balls.NOBALL, PotType.SAFE, PotAction.Switch)

    fun onMissClicked() = updateFrame(crtFrame, Balls.NOBALL, PotType.MISS, PotAction.Switch)

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() = updateFrame(crtFrame, Balls.RED, PotType.ADD_RED, PotAction.Continue)

    fun onRerackClicked() = resetFrame()

    fun onUndoClicked() {
        val lastPot = removeFromFrameStack()
        Timber.e("last pot $lastPot")
        when (lastPot.potType) {
            PotType.HIT -> ballStack.push(if (isColor()) Balls.COLOR else lastPot.ball)
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
        calcPoints(crtFrame, lastPot.ball, lastPot.potType, -1)
        getFrameStatus()
    }

    // Frame Helpers
    private fun updateFrame(currentPlayer: CurrentFrame, ball: Ball, potType: PotType, potAction: PotAction) {
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE) -> ballStack.pop()
            in listOf(PotType.REMOVE_RED, PotType.ADD_RED) -> repeat(2) { ballStack.pop() }
            else -> {
                if (potAction == PotAction.Switch) crtFrame = crtFrame.otherPlayer() // Switch players
                if (ballStack.peek() == Balls.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (ballStack.peek() == Balls.COLOR) ballStack.pop()
            }
        }
        calcPoints(currentPlayer, ball, potType, 1)
        if (ballStack.size == 1 && crtFrame.framePoints == crtFrame.otherPlayer().framePoints) ballStack.push(Balls.BLACK)
        addToFrameStack(currentPlayer, Pot(ball, potType, potAction))
        getFrameStatus()
    }

    private fun calcPoints(crtPlayer: CurrentFrame, ball: Ball, potType: PotType, pol: Int) {
        val points = when (potType) { // foul from sinking the white should equal min ball foul on the table
            PotType.FOUL ->
                if (ballStack.size in 1..4 && ball == Balls.WHITE) ballStack.peek()!!.foulPoints + matchFoulModifier
                else ball.foulPoints + matchFoulModifier
            PotType.REMOVE_RED -> 0
            PotType.FREE -> if (inColors()) 1 else ballStack.peek()!!.points
            else -> ball.points
        }
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE, PotType.ADD_RED) -> {
                crtPlayer.addFramePoints(pol * points)
                crtPlayer.incrementSuccessShots(pol)
            }
            PotType.FOUL -> {
                crtPlayer.otherPlayer().addFramePoints(pol * points)
                crtPlayer.incrementMissedShots(pol)
                crtPlayer.incrementFouls(pol)
            }
            PotType.MISS -> crtPlayer.incrementMissedShots(pol)
            else -> {
            }
        }
    }

    private fun getFrameStatus() {
        if (ballStack.size == 1) endFrame()
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size
        _displayFrameStack.value = frameStack
        _displayPlayer.value = crtFrame
    }

    private fun addToFrameStack(player: CurrentFrame, pot: Pot) {
        when (pot.potType) {
            in listOf(PotType.HIT, PotType.FREE, PotType.ADD_RED) -> {
                if (frameStack.size == 0) frameStack.push(Break(player, ArrayDeque<Pot>()))
                else frameStack.peek()!!.pots.peek()?.let { crtPot ->
                    if (crtPot.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADD_RED)) {
                        frameStack.push(Break(player, ArrayDeque<Pot>()))
                    }
                }
                frameStack.peek()!!.pots.push(pot)
            }
            else -> {
                frameStack.push(Break(player, ArrayDeque<Pot>()))
                frameStack.peek()!!.pots.push(pot)
            }
        }
        frameStack.forEach {
            Timber.e("break $it")
        }
    }

    private fun removeFromFrameStack(): Pot {
        if (frameStack.peek()!!.pots.size == 0) frameStack.pop()
        crtFrame = frameStack.peek()!!.player
        val crtPot: Pot = frameStack.peek()!!.pots.pop()
        if (frameStack.size == 1 && frameStack.peek()!!.pots.size == 0) frameStack.pop()
        return crtPot
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
        if (crtFrame.getFirstPlayer().framePoints > crtFrame.getSecondPlayer().framePoints) crtFrame.getFirstPlayer().incrementMatchPoint()
        else crtFrame.getSecondPlayer().incrementMatchPoint()
        crtMatch.frames.add(crtFrame)
        resetFrame()
    }

    private fun endFrame() {
        val player =
            if (crtFrame.getFirstPlayer().framePoints > crtFrame.getSecondPlayer().framePoints) crtFrame.getFirstPlayer()
            else crtFrame.getSecondPlayer()
        if (player.matchPoints + 1 == matchFrames) assignMatchAction(MatchAction.MATCH_ENDED)
        else assignMatchAction(if (ballStack.size == 1) MatchAction.FRAME_ENDED else MatchAction.END_FRAME)
    }

    fun resetMatch() {
        crtMatch = CurrentMatch(ArrayList())
        crtFrame = if (matchBreaksFirst == 0) CurrentFrame.PlayerA else CurrentFrame.PlayerB
        crtFrame.getFirstPlayer().matchPoints = 0
        crtFrame.getSecondPlayer().matchPoints = 0
        resetFrame()
    }

    private fun resetFrame() {
        if (crtFrame.getFirstPlayer().matchPoints != 0 || crtFrame.getSecondPlayer().matchPoints != 0) crtFrame = crtFrame.otherPlayer()
        crtFrame.getFirstPlayer().framePoints = 0
        crtFrame.getSecondPlayer().framePoints = 0
        ballStack.apply {
            frameStack.clear()
            clear()
            for (ball in listOf(Balls.NOBALL, Balls.BLACK, Balls.PINK, Balls.BLUE, Balls.BROWN, Balls.GREEN, Balls.YELLOW)) push(ball)
            repeat(matchReds) { for (ball in listOf(Balls.COLOR, Balls.RED)) push(ball) }
        }
        getFrameStatus()
    }
}