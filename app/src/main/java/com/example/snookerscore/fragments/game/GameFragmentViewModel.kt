package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch
import java.util.*

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // Frame Observables
    private val _frameState = MutableLiveData<BallType>()
    val frameState: LiveData<BallType> = _frameState

    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _ballStackSize = MutableLiveData<Int>()
    val ballStackSize: LiveData<Int> = _ballStackSize

    private val _displayPlayer = MutableLiveData<CurrentFrame>()
    val displayPlayer: LiveData<CurrentFrame> = _displayPlayer

    private val _displayFrameStack = MutableLiveData<ArrayDeque<Break>>()
    val displayFrameStack: LiveData<ArrayDeque<Break>> = _displayFrameStack

    // Match Observables
    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    // Init variables
    var matchFrames = 0
    private var matchReds = 0
    private var matchFoulModifier = 0
    private var matchBreaksFirst = 0

    fun setMatchRules(matchFrames: Int, matchReds: Int, matchFoulModifier: Int, matchBreaksFirst: Int) {
        this.matchFrames = matchFrames
        this.matchReds = matchReds
        this.matchFoulModifier = matchFoulModifier
        this.matchBreaksFirst = matchBreaksFirst
        resetMatch()
    }

    // Variables
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)
    private var ballStack = ArrayDeque<Ball>()
    private var frameCount = 1
    private lateinit var frameScore: CurrentFrame
    private val frameStack = ArrayDeque<Break>()

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(frameScore, pot.ball, pot.potType, pot.potAction)
        if (removeRed) updateFrame(frameScore, (if (inColors()) ballStack.peek() else Balls.RED), PotType.REMOVE_RED, PotAction.Continue)
        if (freeBall) {
            if (inColors()) ballStack.push(Balls.FREE) else for (ball in listOf(Balls.COLOR, Balls.FREE)) ballStack.push(ball)
            getFrameStatus()
        }
    }

    fun onBallClicked(pot: Pot) = updateFrame(frameScore, pot.ball, pot.potType, PotAction.Continue)

    fun onSafeClicked() = updateFrame(frameScore, Balls.NOBALL, PotType.SAFE, PotAction.Switch)

    fun onMissClicked() = updateFrame(frameScore, Balls.NOBALL, PotType.MISS, PotAction.Switch)

    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun onAddRedClicked() = updateFrame(frameScore, Balls.RED, PotType.ADD_RED, PotAction.Continue)

    fun onRerackClicked() = resetFrame()

    fun onUndoClicked() {
        val lastPot = removeFromFrameStack()
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
            in listOf(PotType.MISS, PotType.SAFE) -> if (isColor()) ballStack.push(Balls.COLOR)
            else -> {}
        }
        calcPoints(frameScore, lastPot.ball, lastPot.potType, -1)
        getFrameStatus()
    }

    // Frame Helpers
    private fun updateFrame(currentPlayer: CurrentFrame, ball: Ball, potType: PotType, potAction: PotAction) {
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE) -> ballStack.pop()
            in listOf(PotType.REMOVE_RED, PotType.ADD_RED) -> repeat(2) { ballStack.pop() }
            else -> {
                if (potAction == PotAction.Switch) frameScore = frameScore.otherPlayer() // Switch players
                if (ballStack.peek() == Balls.FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (ballStack.peek() == Balls.COLOR) ballStack.pop()
            }
        }
        calcPoints(currentPlayer, ball, potType, 1)
        if (ballStack.size == 1 && frameScore.framePoints == frameScore.otherPlayer().framePoints) ballStack.push(Balls.BLACK)
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

    private fun addToFrameStack(player: CurrentFrame, pot: Pot) {
        if (pot.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADD_RED)
            || frameStack.size == 0
            || frameStack.peek()!!.pots.peek()?.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADD_RED)
        ) frameStack.push(Break(player, ArrayDeque<Pot>(), 0))
        frameStack.peek()!!.pots.push(pot)
        frameStack.peek()!!.breakSize += pot.ball.points
        player.findMaxBreak(frameStack)
    }

    private fun removeFromFrameStack(): Pot {
        if (frameStack.peek()!!.pots.size == 0) frameStack.pop()
        frameScore = frameStack.peek()!!.player
        val crtPot: Pot = frameStack.peek()!!.pots.pop()
        frameStack.peek()!!.breakSize -= crtPot.ball.points
        frameScore.findMaxBreak(frameStack)
        if (frameStack.size == 1 && frameStack.peek()!!.pots.size == 0) frameStack.pop()
        return crtPot
    }

    private fun getFrameStatus() {
        if (ballStack.size == 1) endFrame()
        _frameState.value = ballStack.peek()!!.ballType
        _ballStackSize.value = ballStack.size
        _displayFrameStack.value = frameStack
        _displayPlayer.value = frameScore
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
    private fun endFrame() {
        val player =
            if (frameScore.getFirstPlayer().framePoints > frameScore.getSecondPlayer().framePoints) frameScore.getFirstPlayer()
            else frameScore.getSecondPlayer()
        if (player.matchPoints + 1 == matchFrames) assignMatchAction(MatchAction.MATCH_ENDED)
        else assignMatchAction(if (ballStack.size == 1) MatchAction.FRAME_ENDED else MatchAction.END_FRAME)
    }

    fun matchEnded() {
        frameEnded()
    }

    fun frameEnded() {
        if (frameScore.getFirstPlayer().framePoints > frameScore.getSecondPlayer().framePoints) frameScore.getFirstPlayer().incrementMatchPoint()
        else frameScore.getSecondPlayer().incrementMatchPoint()
        viewModelScope.launch {
            snookerRepository.addFrames(Frame(frameCount, listOf(frameScore.getFirstPlayer().asFrameScore(), frameScore.getSecondPlayer().asFrameScore())))
        }
        resetFrame()
        frameCount += 1
    }

    fun resetMatch() {
        frameScore = if (matchBreaksFirst == 0) CurrentFrame.PlayerA else CurrentFrame.PlayerB
        frameScore.getFirstPlayer().resetMatchScore()
        frameScore.getSecondPlayer().resetMatchScore()
        frameCount = 1
        resetFrame()
        viewModelScope.launch {
            snookerRepository.removeFrames()
        }
    }

    private fun resetFrame() {
        if (frameScore.getFirstPlayer().matchPoints != 0 || frameScore.getSecondPlayer().matchPoints != 0) frameScore = frameScore.otherPlayer()
        frameScore.getFirstPlayer().resetFrameScore()
        frameScore.getSecondPlayer().resetFrameScore()
        ballStack.apply {
            frameStack.clear()
            clear()
            for (ball in listOf(Balls.NOBALL, Balls.BLACK, Balls.PINK, Balls.BLUE, Balls.BROWN, Balls.GREEN, Balls.YELLOW)) push(ball)
            repeat(matchReds) { for (ball in listOf(Balls.COLOR, Balls.RED)) push(ball) }
        }
        getFrameStatus()
    }

    private fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }
}