package com.example.snookerscore.fragments.game

import android.app.Application
import androidx.lifecycle.*
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.PotType.*
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch
import kotlin.math.max

class GameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // Observables
    private val _displayBallStack = MutableLiveData<MutableList<Ball>>()
    val displayBallStack: LiveData<MutableList<Ball>> = _displayBallStack

    private val _displayScore = MutableLiveData<CurrentScore>()
    val displayScore: LiveData<CurrentScore> = _displayScore

    private val _displayFrameStack = MutableLiveData<MutableList<Break>>()
    val displayFrameStack: LiveData<MutableList<Break>> = _displayFrameStack

    private var _frameCount = MutableLiveData(1)
    val frameCount: LiveData<Int> = _frameCount

    val dbBreaks = snookerRepository.currentBreaks

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    // Init game settings
    var matchFrames = 0
    private var matchReds = 0
    private var matchFoul = 0
    private var matchFirst = 0

    fun resetMatch(matchFrames: Int, matchReds: Int, matchFoul: Int, matchFirst: Int) {
        this.matchFrames = matchFrames
        this.matchReds = matchReds
        this.matchFoul = matchFoul
        this.matchFirst = matchFirst
        resetMatchScore()
    }

    fun setSavedStateRules() {
        savedStateHandle["matchFrames"] = matchFrames
        savedStateHandle["matchReds"] = matchReds
        savedStateHandle["matchFoul"] = matchFoul
        savedStateHandle["matchFirst"] = matchFirst
        savedStateHandle["matchCrtPlayer"] = score.getPlayerAsInt()
    }

    fun getSavedStateRules() {
        matchFrames = savedStateHandle.get("matchFrames") ?: 0
        matchReds = savedStateHandle.get("matchReds") ?: 0
        matchFoul = savedStateHandle.get("matchFoul") ?: 0
        matchFirst = savedStateHandle.get("matchFirst") ?: 0
        score = score.getPlayerFromInt(savedStateHandle.get("matchCrtPlayer") ?: matchFirst)
    }

    // Other variables
    private var ballStack: MutableList<Ball> = mutableListOf()
    private var score: CurrentScore = CurrentScore.PlayerA
    private var frameStack: MutableList<Break> = mutableListOf()

    fun setBallStack(ballStack: MutableList<Ball>) {
        this.ballStack = ballStack
        getFrameStatus()
    }

    fun setScore(score: CurrentScore) {
        this.score = score
        getFrameStatus()
    }

    fun setFrameStack(frameStack: MutableList<Break>) {
        this.frameStack = frameStack
        getFrameStatus()
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
        if (pot.potAction == PotAction.SWITCH) switchPlayers()
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
        ) frameStack.add(
            Break(
                1 + (frameStack.lastOrNull()?.breakId ?: 0),
                score.getPlayerAsInt(),
                _frameCount.value!!,
                mutableListOf(),
                0
            )
        )
        frameStack.last().pots.add(pot)
        if (pot.potType in listOf(HIT, FREE, ADDRED)) frameStack.last().breakSize += pot.ball.points
    }

    private fun removeFromFrameStack(): Pot {
        if (score != score.getPlayerFromInt(frameStack.last().player)) switchPlayers()
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
        getFirst().frameId = _frameCount.value!!
        getSecond().frameId = _frameCount.value!!
        switchPlayers()
        viewModelScope.launch {
            snookerRepository.addFrames(score, _frameCount.value!!)
            this@GameViewModel.resetFrameScore()
            _frameCount.value = _frameCount.value!!.plus(1)
        }
    }

    fun resetMatchScore() {
        score = score.getPlayerFromInt(matchFirst)
        score.getFirst().resetMatchScore()
        score.getSecond().resetMatchScore()
        _frameCount.value = 1
        resetFrameScore()
        viewModelScope.launch {
            snookerRepository.removeFrames()
            snookerRepository.deleteCurrentMatch()
        }
    }

    fun resetFrameScore() {
        score.getFirst().resetFrameScore()
        score.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        addBalls(NOBALL, BLACK, PINK, BLUE, BROWN, GREEN, YELLOW)
        repeat(matchReds) { addBalls(COLOR, RED) }
        getFrameStatus()
    }

    // Helpers
    private fun switchPlayers() {
        score = score.getOther()
        savedStateHandle["matchCrtPlayer"] = score.getPlayerAsInt()
    }

    private fun getFrameStatus() {
        _displayBallStack.value = ballStack
        _displayFrameStack.value = frameStack
        _displayScore.value = score
        score.findMaxBreak(frameStack)
    }

    fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

    private fun inColors(): Boolean = ballStack.size <= 7
    private fun nextIsColor(): Boolean = ballStack.size in (7..37).filter { it % 2 != 0 }
    private fun removeBall(times: Int = 1) = repeat(times) { ballStack.removeLast() }
    private fun previousIsRed() = frameStack.last().pots.last().ball == RED
    private fun addBalls(vararg balls: Ball) {
        for (ball in balls) ballStack.add(ball)
    }
}