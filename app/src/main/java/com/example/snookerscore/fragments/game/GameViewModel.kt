package com.example.snookerscore.fragments.game

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.snookerscore.R
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.PotType.*
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository
) : AndroidViewModel(application) {

    // Observables
    private val _displayBallStack = MutableLiveData<MutableList<Ball>>()
    val displayBallStack: LiveData<MutableList<Ball>> = _displayBallStack

    private val _displayScore = MutableLiveData<CurrentScore>()
    val displayScore: LiveData<CurrentScore> = _displayScore

    private val _displayFrameStack = MutableLiveData<MutableList<Break>>()
    val displayFrameStack: LiveData<MutableList<Break>> = _displayFrameStack

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    // Variables
    private var ballStack: MutableList<Ball> = mutableListOf()
    private var score: CurrentScore = CurrentScore.PlayerA
    private var frameStack: MutableList<Break> = mutableListOf()
    private val sharedPref: SharedPreferences = application.getSharedPreferences(
        application.applicationContext.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )
    private var frameCount = 1

    // Init game settings
    private var matchFrames = 0
    private var matchReds = 0
    private var matchFoul = 0
    private var matchFirst = 0

    fun setSavedStateRules(): SharedPreferences.Editor = sharedPref.edit().apply {
        getApplication<Application>().resources.apply {
            putBoolean(getString(R.string.shared_pref_match_is_saved), true)
            putInt(getString(R.string.shared_pref_match_frames), matchFrames)
            putInt(getString(R.string.shared_pref_match_reds), matchReds)
            putInt(getString(R.string.shared_pref_match_foul), matchFoul)
            putInt(getString(R.string.shared_pref_match_first), matchFirst)
            putInt(getString(R.string.shared_pref_match_crt_player), score.getPlayerAsInt())
            apply()
        }
    }

    fun getSavedStateRules() = sharedPref.apply {
        getApplication<Application>().resources.apply {
            matchFrames = getInt(getString(R.string.shared_pref_match_frames), 0)
            matchReds = getInt(getString(R.string.shared_pref_match_reds), 0)
            matchFoul = getInt(getString(R.string.shared_pref_match_foul), 0)
            matchFirst = getInt(getString(R.string.shared_pref_match_first), 0)
            score = score.getPlayerFromInt(getInt(getString(R.string.shared_pref_match_crt_player), 0))
            getFrameStatus()
        }
    }

    fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

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
    fun handleFoulEvent(pot: Pot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(pot)
        if (removeRed) updateFrame(Pot.REMOVERED)
        if (freeBall) {
            if (ballStack.inColors()) ballStack.addBalls(FREEBALL()) else ballStack.addBalls(COLOR(), FREEBALL())
            getFrameStatus()
        }
    }

    fun updateFrame(pot: Pot) = ballStack.apply {
        val newPot = score.calculatePoints(pot, 1, this.last(), matchFoul)
        frameStack.addToFrameStack(newPot, score.getPlayerAsInt(), frameCount)
        when (pot.potType) {
            in listOf(HIT, FREE) -> removeBall()
            in listOf(REMOVERED, ADDRED) -> removeBall(2)
            else -> {
                if (last() is FREEBALL) {
                    frameStack.addToFrameStack(Pot.FREEMISS, score.getPlayerAsInt(), frameCount)
                    removeBall(if (inColors()) 1 else 2)
                }
                if (last() is COLOR) removeBall()
            }
        }
        if (size == 1) if (score.isFrameEqual()) addBalls(BLACK()) else endFrame()
        if (pot.potAction == PotAction.SWITCH) score = score.getOther()
        getFrameStatus()
    }

    fun undo(): Any = ballStack.apply {
        score = score.getPlayerFromInt(frameStack.last().player)
        val lastPot = frameStack.removeFromFrameStack()
        when (lastPot.potType) {
            HIT -> addBalls(if (isNextColor()) COLOR() else lastPot.ball)
            ADDRED -> addBalls(RED(), COLOR())
            FREE -> {
                if (inColors()) addBalls(FREEBALL()) else addBalls(COLOR(), FREEBALL())
                undo()
            }
            REMOVERED -> {
                if (last() is FREEBALL) removeBall(if (inColors()) 1 else 2)
                if (isNextColor()) addBalls(COLOR(), RED()) else addBalls(RED(), COLOR())
                undo()
            }
            FOUL -> {
                if (last() is FREEBALL) removeBall(if (inColors()) 1 else 2)
                if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            }
            SAFE -> if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            MISS -> if (frameStack.isPreviousRed() && isNextColor()) {
                addBalls(COLOR())
            }
        }
        score.calculatePoints(lastPot, -1, ballStack.last(), matchFoul)
        getFrameStatus()
    }

    // Match functions
    fun endFrame() = assignMatchAction(
        when {
            score.getWinner().matchPoints + 1 == matchFrames -> MatchAction.MATCH_END_CONFIRM
            ballStack.size == 1 -> MatchAction.FRAME_END_CONFIRM
            else -> MatchAction.FRAME_END_QUERY
        }
    )

    fun frameEnded() = score.apply {
        getWinner().addMatchPoint()
        getFirst().frameId = frameCount
        getSecond().frameId = frameCount
        score = score.getOther()
        viewModelScope.launch {
            snookerRepository.addFrames(score)
            this@GameViewModel.resetFrame()
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
            snookerRepository.deleteMatchFrames()
            snookerRepository.deleteCurrentMatch()
        }
    }

    fun resetFrame() {
        score.getFirst().resetFrameScore()
        score.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        ballStack.addBalls(NOBALL(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
        repeat(matchReds) { ballStack.addBalls(COLOR(), RED()) }
        getFrameStatus()
    }

    private fun getFrameStatus() {
        _displayBallStack.value = ballStack
        _displayFrameStack.value = frameStack
        _displayScore.value = score
        score.findMaxBreak(frameStack)
    }
}