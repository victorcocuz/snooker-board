package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.fragments.game.Ball.*
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch
import java.util.*

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // Observables
    private val _displayBallStack = MutableLiveData<ArrayDeque<Ball>>()
    val displayBallStack: LiveData<ArrayDeque<Ball>> = _displayBallStack

    private val _displayPlayer = MutableLiveData<CurrentScore>()
    val displayPlayer: LiveData<CurrentScore> = _displayPlayer

    private val _displayFrameStack = MutableLiveData<ArrayDeque<Break>>()
    val displayFrameStack: LiveData<ArrayDeque<Break>> = _displayFrameStack

    // Events
    private val _eventFoul = MutableLiveData<Event<Unit>>()
    val eventFoul: LiveData<Event<Unit>> = _eventFoul

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventMatchActionConfirmed = MutableLiveData<Event<MatchAction>>()
    val eventMatchActionConfirmed: LiveData<Event<MatchAction>> = _eventMatchActionConfirmed

    private val _eventCancelDialog = MutableLiveData<Event<Unit>>()
    val eventCancelDialog: LiveData<Event<Unit>> = _eventCancelDialog

    // Init game settings
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

    // Other variables
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)
    private var frameCount = 1
    private var ballStack = ArrayDeque<Ball>()
    private lateinit var score: CurrentScore
    private val frameStack = ArrayDeque<Break>()

    // Event handlers
    fun onFoulClicked() {
        _eventFoul.value = Event(Unit)
    }

    fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

    fun onGenDialogCancelClicked() {
        _eventCancelDialog.value = Event(Unit)
    }

    fun onGenDialogConfirmed(matchAction: MatchAction) {
        _eventMatchActionConfirmed.value = Event(matchAction)
    }

    // Handler functions
    fun handleFoulDialog(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(pot)
        if (removeRed) updateFrame(Pot.REMOVERED((if (inColors()) ballStack.peek() else RED)))
        if (freeBall) {
            if (inColors()) ballStack.push(FREE) else for (ball in listOf(COLOR, FREE)) ballStack.push(ball)
            getFrameStatus()
        }
    }

    fun undo() {
        val lastPot = removeFromFrameStack()
        when (lastPot.potType) {
            PotType.HIT -> ballStack.push(if (isColor()) COLOR else lastPot.ball)
            PotType.ADDRED -> for (ball in listOf(RED, COLOR)) ballStack.push(ball)
            PotType.FOUL -> {
                if (ballStack.peek()!! == FREE) repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                if (isColor()) ballStack.push(COLOR)
            }
            PotType.REMOVERED -> {
                undo()
                for (ball in if (isColor()) listOf(COLOR, RED) else listOf(RED, COLOR)) ballStack.push(ball)
            }
            PotType.SAFE -> if (isColor()) ballStack.push(COLOR)
            PotType.MISS -> if (isColor()) ballStack.push(COLOR)
            PotType.FREE -> {
                if (inColors()) ballStack.push(FREE) else for (ball in listOf(COLOR, FREE)) ballStack.push(ball)
                undo()
            }
        }
        calcPoints(lastPot.ball, lastPot.potType, -1)
        getFrameStatus()
    }

    // Frame Helpers
    fun updateFrame(pot: Pot) {
        addToFrameStack(pot)
        when (pot.potType) {
            in listOf(PotType.HIT, PotType.FREE) -> ballStack.pop()
            in listOf(PotType.REMOVERED, PotType.ADDRED) -> repeat(2) { ballStack.pop() }
            else -> {
                if (ballStack.peek() == FREE) {
                    addToFrameStack(Pot.FREEMISS)
                    repeat(if (inColors()) 1 else 2) { ballStack.pop() }
                }
                if (ballStack.peek() == COLOR) ballStack.pop()
            }
        }
        calcPoints(pot.ball, pot.potType, 1)
        if (ballStack.size == 1 && score.framePoints == score.getOther().framePoints) ballStack.push(BLACK)
        getFrameStatus()
        if (pot.potAction == PotAction.SWITCH) score = score.getOther() // Switch players
    }

    private fun calcPoints(ball: Ball, potType: PotType, pol: Int) {
        val points = when (potType) {
            PotType.FOUL -> // foul from sinking the white should equal min ball foul on the table
                if (ballStack.size in 1..4 && ball == WHITE) ballStack.peek()!!.foulPoints + matchFoulModifier
                else ball.foulPoints + matchFoulModifier
            PotType.REMOVERED -> 0
            PotType.FREE -> if (ball == NOBALL) 0 else if (inColors()) 1 else ballStack.peek()!!.points
            else -> ball.points
        }
        when (potType) {
            in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED) -> {
                score.incrementFramePoints(pol * points)
                score.incrementSuccessShots(pol)
            }
            PotType.FOUL -> {
                score.getOther().incrementFramePoints(pol * points)
                score.incrementMissedShots(pol)
                score.incrementFouls(pol)
            }
            PotType.MISS -> score.incrementMissedShots(pol)
            else -> return
        }
    }

    private fun addToFrameStack(pot: Pot) {
        if (pot.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
            || frameStack.size == 0
            || frameStack.peek()!!.pots.peek()?.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
        ) frameStack.push(Break(score.getPlayerAsInt(), ArrayDeque<Pot>(), 0))
        frameStack.peek()!!.pots.push(pot)
        frameStack.peek()!!.breakSize += pot.ball.points
    }

    private fun removeFromFrameStack(): Pot {
        score = score.getPlayerFromInt(frameStack.peek()!!.player)
        val crtPot: Pot = frameStack.peek()!!.pots.pop()
        frameStack.peek()!!.breakSize -= crtPot.ball.points
        if (frameStack.peek()!!.pots.size == 0) frameStack.pop()
        if (frameStack.size == 1 && frameStack.peek()!!.pots.size == 0) frameStack.pop()
        return crtPot
    }

    // Match functions
    fun endFrame() {
        val player =
            if (score.getFirst().framePoints > score.getSecond().framePoints) score.getFirst()
            else score.getSecond()
        if (player.matchPoints + 1 == matchFrames) assignMatchAction(MatchAction.MATCH_ENDED)
        else assignMatchAction(if (ballStack.size == 1) MatchAction.FRAME_ENDED else MatchAction.END_FRAME)
    }

    fun frameEnded() {
        if (score.getFirst().framePoints > score.getSecond().framePoints) score.getFirst().incrementMatchPoint()
        else score.getSecond().incrementMatchPoint()
        score.getFirst().incrementFrameCount()
        score.getSecond().incrementFrameCount()
        viewModelScope.launch {
            snookerRepository.addFrames(score)
            resetFrame()
            frameCount += 1
        }
    }

    fun resetMatch() {
        score = if (matchBreaksFirst == 0) CurrentScore.PlayerA else CurrentScore.PlayerB
        score.getFirst().resetMatchScore()
        score.getSecond().resetMatchScore()
        frameCount = 1
        resetFrame()
        viewModelScope.launch {
            snookerRepository.removeFrames()
        }
    }

    fun resetFrame() {
        if (score.getFirst().matchPoints != 0 || score.getSecond().matchPoints != 0) score = score.getOther()
        score.getFirst().resetFrameScore()
        score.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        for (ball in listOf(NOBALL, BLACK, PINK, BLUE, BROWN, GREEN, YELLOW)) ballStack.push(ball)
        repeat(matchReds) { for (ball in listOf(COLOR, RED)) ballStack.push(ball) }
        getFrameStatus()
    }

    // Helpers
    private fun getFrameStatus() {
        if (ballStack.size == 1) endFrame()
        _displayBallStack.value = ballStack
        _displayFrameStack.value = frameStack
        _displayPlayer.value = score
        score.findMaxBreak(frameStack)
    }

    private fun inColors(): Boolean = ballStack.size <= 7
    private fun isColor(): Boolean = ballStack.size in (7..35).filter { it % 2 != 0 }
}