package com.example.snookerscore.fragments.game

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.snookerscore.R
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.DomainBall.*
import com.example.snookerscore.domain.PotType.*
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.Event
import kotlinx.coroutines.launch
import timber.log.Timber

class GameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository
) : AndroidViewModel(application) {

    // Observables
    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame

    private val _displayScore = MutableLiveData<CurrentScore>()
    val displayScore: LiveData<CurrentScore> = _displayScore

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction

    private val _eventFrameUpdated = MutableLiveData<Event<Unit>>()
    val eventFrameUpdated: LiveData<Event<Unit>> = _eventFrameUpdated

    // Variables
    private var ballStack: MutableList<DomainBall> = mutableListOf()
    private var score: CurrentScore = CurrentScore.PlayerA
    private var frameStack: MutableList<DomainBreak> = mutableListOf()
    private val sharedPref: SharedPreferences = application.getSharedPreferences(
        application.applicationContext.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )
    private var frameCount = 1

    // Match settings
    private var matchFrames = 0
    private var matchReds = 0
    private var matchFoul = 0
    private var matchFirst = 0

    suspend fun saveMatch() {
        if (score.isMatchInProgress()) {
            Timber.e("saving match")
            snookerRepository.deleteCurrentFrame()
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            sharedPref.edit().apply {
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
        }
    }

    fun loadMatch(frame: DomainFrame) {
        Timber.e("framescore ${frame.frameScore}")
        score = frame.frameScore.asCurrentScore() ?: score
        frameStack = frame.frameStack
        ballStack = frame.ballStack
        getSavedStateRules()
        updateFrameStatus()
    }

    fun resetMatch() {
        getSavedStateRules()
        score.getFirst().resetMatchScore()
        score.getSecond().resetMatchScore()
        frameCount = 1
        resetFrame()
        sharedPref.edit().putBoolean(getApplication<Application>().resources.getString(R.string.shared_pref_match_is_saved), false).apply()
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
        updateFrameStatus()
    }

    private fun getSavedStateRules() = sharedPref.apply {
        getApplication<Application>().resources.apply {
            matchFrames = getInt(getString(R.string.shared_pref_match_frames), 0)
            matchReds = getInt(getString(R.string.shared_pref_match_reds), 0)
            matchFoul = getInt(getString(R.string.shared_pref_match_foul), 0)
            matchFirst = getInt(getString(R.string.shared_pref_match_first), 0)
            score = score.getPlayerFromInt(getInt(getString(R.string.shared_pref_match_crt_player), 0))
            Timber.e("score is $score")
        }
    }

    fun resetFrame() {
        if (score.isMatchInProgress()) {
            matchFirst = if (matchFirst == 0) 1 else 0
            score = score.getPlayerFromInt(matchFirst)
        } else {
            score = score.getPlayerFromInt(matchFirst)
        }
        score.getFirst().resetFrameScore()
        score.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        ballStack.addBalls(WHITE(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
        repeat(matchReds) { ballStack.addBalls(COLOR(), RED()) }
        updateFrameStatus()
    }

    fun endFrame() {
        when {
            _displayFrame.value!!.isFrameInProgress() -> assignMatchAction(
                if (score.getWinner().matchPoints + 1 == matchFrames) MatchAction.MATCH_END_QUERY else MatchAction.FRAME_END_QUERY
            )
            score.getWinner().matchPoints + 1 == matchFrames -> assignMatchAction(MatchAction.MATCH_END_CONFIRM)
            else -> frameEnded()
        }
    }

    fun endMatch() {
        when {
            _displayFrame.value!!.isFrameInProgress() -> assignMatchAction(MatchAction.MATCH_END_QUERY)
            else -> assignMatchAction(MatchAction.MATCH_END_CONFIRM)
        }
    }

    fun frameEnded() = score.apply {
        getWinner().addMatchPoint()
        getFirst().frameId = frameCount
        getSecond().frameId = frameCount
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            this@GameViewModel.resetFrame()
            frameCount += 1
            _eventFrameUpdated.value = Event(Unit)
        }
    }

    fun assignMatchAction(matchAction: MatchAction) {
        _eventMatchAction.value = Event(matchAction)
    }

    // Handler functions
    fun handleFoulEvent(pot: DomainPot, removeRed: Boolean, freeBall: Boolean) {
        updateFrame(pot)
        if (removeRed) updateFrame(DomainPot.REMOVERED)
        if (freeBall) {
            if (ballStack.inColors()) ballStack.addBalls(FREEBALL()) else ballStack.addBalls(COLOR(), FREEBALL())
            updateFrameStatus()
        }
    }

    fun updateFrame(pot: DomainPot) = ballStack.apply {
        val newPot = score.calculatePoints(pot, 1, this.last(), matchFoul)
        frameStack.addToFrameStack(newPot, score.getPlayerAsInt(), frameCount)
        when (pot.potType) {
            in listOf(HIT, FREE) -> removeBall()
            in listOf(REMOVERED, ADDRED) -> removeBall(2)
            else -> {
                if (last() is FREEBALL) {
                    frameStack.addToFrameStack(DomainPot.FREEMISS, score.getPlayerAsInt(), frameCount)
                    removeBall(if (inColors()) 1 else 2)
                }
                if (last() is COLOR) removeBall()
            }
        }
        if (size == 1) if (score.isFrameEqual()) addBalls(BLACK()) else assignMatchAction(MatchAction.FRAME_END_CONFIRM)
        if (pot.potAction == PotAction.SWITCH) score = score.getOther()
        updateFrameStatus()
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
        updateFrameStatus()
    }

    // Match functions
    private fun updateFrameStatus() {
        _displayScore.value = score
        _displayFrame.value = DomainFrame(
            frameCount,
            mutableListOf(
                score.getFirst().asDomainPlayerScore(),
                score.getSecond().asDomainPlayerScore()
            ),
            frameStack,
            ballStack
        )
        score.findMaxBreak(frameStack)
    }
}