package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.PotType.*
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch
import kotlin.math.max

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {

    // Observables
    private val _displayBallStack = MutableLiveData<MutableList<Ball>>()
    val displayBallStack: LiveData<MutableList<Ball>> = _displayBallStack

    private val _displayPlayer = MutableLiveData<CurrentScore>()
    val displayPlayer: LiveData<CurrentScore> = _displayPlayer

    private val _displayFrameStack = MutableLiveData<MutableList<Break>>()
    val displayFrameStack: LiveData<MutableList<Break>> = _displayFrameStack

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
    private var matchFoul = 0
    private var matchFirst = 0

    fun setMatchRules(matchFrames: Int, matchReds: Int, matchFoul: Int, matchFirst: Int) {
        this.matchFrames = matchFrames
        this.matchReds = matchReds
        this.matchFoul = matchFoul
        this.matchFirst = matchFirst
        resetMatch()
    }

    // Other variables
    private val database = SnookerDatabase.getDatabase(application)
    private val snookerRepository = SnookerRepository(database)
    private var frameCount = 1
    private var ballStack : MutableList<Ball> = mutableListOf()
    private var score: CurrentScore = CurrentScore.PlayerA
    private val frameStack : MutableList<Break> = mutableListOf()

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
        if (removeRed) updateFrame(Pot.REMOVERED)
        if (freeBall) {
            if (inColors()) addBalls(FREEBALL) else addBalls(COLOR, FREEBALL)
            getFrameStatus()
        }
    }

    fun undo() {
        val lastPot = removeFromFrameStack()
        when (lastPot.potType) {
            HIT -> addBalls(if (nextIsColor()) COLOR else lastPot.ball)
            ADDRED -> addBalls(RED, COLOR)
            FREE -> {
                if (inColors()) addBalls(FREEBALL) else addBalls(COLOR, FREEBALL)
                undo()
            }
            REMOVERED -> {
                if (ballStack.last() == FREEBALL) removeBall(if (inColors()) 1 else 2)
                if (nextIsColor()) addBalls(COLOR, RED) else addBalls(RED, COLOR)
                undo()
            }
            FOUL -> {
                if (ballStack.last() == FREEBALL) removeBall(if (inColors()) 1 else 2)
                if (previousIsRed() && nextIsColor()) addBalls(COLOR)
            }
            SAFE -> if (previousIsRed() && nextIsColor()) addBalls(COLOR)
            MISS -> if (previousIsRed() && nextIsColor()) {
                addBalls(COLOR)
            }
        }
        calcPoints(lastPot.ball, lastPot.potType, -1)
        getFrameStatus()
    }

    // Frame Helpers
    fun updateFrame(pot: Pot) {
        addToFrameStack(pot)
        when (pot.potType) {
            in listOf(HIT, FREE) -> removeBall()
            in listOf(REMOVERED, ADDRED) -> removeBall(2)
            else -> {
                if (ballStack.last() == FREEBALL) {
                    addToFrameStack(Pot.FREEMISS)
                    removeBall(if (inColors()) 1 else 2)
                }
                if (ballStack.last() == COLOR) removeBall()
            }
        }
        if (ballStack.size == 1) if (score.isFrameEqual()) addBalls(BLACK) else endFrame()
        calcPoints(pot.ball, pot.potType, 1)
        if (pot.potAction == PotAction.SWITCH) score = score.getOther() // Switch players
        getFrameStatus()
    }

    private fun calcPoints(ball: Ball, potType: PotType, pol: Int) = score.apply {
        when (potType) {
            in listOf(HIT, FREE, ADDRED) -> {
                addFramePoints(
                    pol * if (ball == FREEBALL) {
                        ballStack.last().points
                    } else ball.points
                )
                addSuccessShots(pol)
            }
            FOUL -> {
                getOther().addFramePoints(pol * (matchFoul + if (ball == WHITE) max(ballStack.last().foul, 4) else ball.foul))
                addMissedShots(pol)
                addFouls(pol)
            }
            MISS -> addMissedShots(pol)
            else -> {
            }
        }
    }

    private fun addToFrameStack(pot: Pot) {
        if (pot.potType !in listOf(HIT, FREE, ADDRED)
            || frameStack.size == 0
            || frameStack.last().pots.last().potType !in listOf(HIT, FREE, ADDRED)
            || frameStack.last().player != score.getPlayerAsInt()
        ) frameStack.add(Break(score.getPlayerAsInt(), 0,mutableListOf(), 0))
        frameStack.last().pots.add(pot)
        if (pot.potType in listOf(HIT, FREE, ADDRED)) frameStack.last().breakSize += pot.ball.points
    }

    private fun removeFromFrameStack(): Pot {
        score = score.getPlayerFromInt(frameStack.last().player)
        val crtPot = frameStack.last().pots.removeLast()
        if (crtPot.potType in listOf(HIT, FREE, ADDRED)) frameStack.last().breakSize -= crtPot.ball.points
        if (frameStack.last().pots.size == 0) frameStack.removeLast()
        if (frameStack.size == 1 && frameStack.last().pots.size == 0) frameStack.removeLast()
        return crtPot
    }

    // Match functions
    fun endFrame() = assignMatchAction(
        when {
            score.getWinner().matchPoints + 1 == matchFrames -> MatchAction.MATCH_ENDED
            ballStack.size == 1 -> MatchAction.FRAME_ENDED
            else -> MatchAction.END_FRAME
        }
    )

    fun frameEnded() = score.apply {
        getWinner().addMatchPoint()
        getFirst().addFrameCount()
        getSecond().addFrameCount()
        score = this.getOther()
        viewModelScope.launch {
            snookerRepository.addFrames(score)
            resetFrame()
            frameCount += 1
        }
    }

    fun resetMatch() {
        score = score.getPlayerFromInt(matchFirst)
        score.getFirst().resetMatchScore()
        score.getSecond().resetMatchScore()
        frameCount = 1
        resetFrame()
        viewModelScope.launch {
            snookerRepository.removeFrames()
        }
    }

    fun resetFrame() {
        score.getFirst().resetFrameScore()
        score.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        addBalls(NOBALL, BLACK, PINK, BLUE, BROWN, GREEN, YELLOW)
        repeat(matchReds) { addBalls(COLOR, RED) }
        getFrameStatus()
    }

    // Helpers
    private fun getFrameStatus() {
        _displayBallStack.value = ballStack
        _displayFrameStack.value = frameStack
        _displayPlayer.value = score
        score.findMaxBreak(frameStack)

    }

    private fun inColors(): Boolean = ballStack.size <= 7
    private fun nextIsColor(): Boolean = ballStack.size in (7..37).filter { it % 2 != 0 }
    private fun removeBall(times: Int = 1) = repeat(times) { ballStack.removeLast() }
    private fun addBalls(vararg balls: Ball) {
        for (ball in balls) ballStack.add(ball)
    }
    private fun previousIsRed() = frameStack.last().pots.last().ball == RED
}