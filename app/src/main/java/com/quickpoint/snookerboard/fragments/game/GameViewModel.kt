package com.quickpoint.snookerboard.fragments.game

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val snookerRepository: SnookerRepository
) : AndroidViewModel(application) {

    // Observables
    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    private val _displayScore = MutableLiveData<CurrentPlayer>()
    val displayScore: LiveData<CurrentPlayer> = _displayScore
    private fun updateFrameInfo() {
        _displayScore.value = player
        _displayFrame.value = DomainFrame(
            frameCount,
            ballStack,
            mutableListOf(
                player.getFirst().asDomainPlayerScore(),
                player.getSecond().asDomainPlayerScore()
            ),
            frameStack,
            frameMax
        )
    }

    private val _eventGameAction = MutableLiveData<Event<MatchAction>>()
    val eventGameAction: LiveData<Event<MatchAction>> = _eventGameAction
    fun assignEventGameAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventGameAction.value = Event(matchAction)
    }

    private val _eventFrameResetComplete = MutableLiveData<Event<Unit>>()
    val eventFrameResetComplete: LiveData<Event<Unit>> = _eventFrameResetComplete // Refresh game options menu after each frame reset
    private fun assignEventFrameResetComplete() {
        _eventFrameResetComplete.value = Event(Unit)
    }

    private val _isFrameUpdateInProgress = MutableLiveData(false)
    val isFrameUpdateInProgress: LiveData<Boolean> = _isFrameUpdateInProgress // Deactivate all buttons & options menu if frame is updating

    // Variables
    private var player: CurrentPlayer = CurrentPlayer.PLAYER01
    private var ballStack: MutableList<DomainBall> = mutableListOf()
    private var frameStack: MutableList<DomainBreak> = mutableListOf()
    private val sharedPref: SharedPreferences = application.getSharedPref()
    private var frameCount = 1
    private var frameMax = 0
    private var matchFrames = 0
    private var matchReds = 0
    private var matchFoul = 0
    private var matchFirst = 0

    // Match actions
    fun startNewMatch() { // When actioned from main menu
        resetMatch()
        sharedPref.setMatchInProgress(true)
    }

    fun cancelMatch() { // When actioned from options menu
        resetMatch()
        sharedPref.setMatchInProgress(false)
    }

    suspend fun saveMatch() { // The match should be saved every time the back button is pressed or when instance state is saved
        if (player.isMatchInProgress()) {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            sharedPref.edit().apply {
                getApplication<Application>().resources.apply {
                    putInt(getString(R.string.shared_pref_match_frames), matchFrames)
                    putInt(getString(R.string.shared_pref_match_reds), matchReds)
                    putInt(getString(R.string.shared_pref_match_foul), matchFoul)
                    putInt(getString(R.string.shared_pref_match_first), matchFirst)
                    putInt(getString(R.string.shared_pref_match_crt_player), player.getPlayerAsInt())
                    putInt(getString(R.string.shared_pref_match_crt_frame), frameCount)
                    apply()
                }
            }
        } else {
            sharedPref.setMatchInProgress(false)
        }
    }

    fun loadMatch(frame: DomainFrame?) = frame?.let { // When actioned from main menu
        viewModelScope.launch {
            snookerRepository.deleteCurrentFrame(frameCount)
        }
        player = it.frameScore.asCurrentScore() ?: player
        frameStack = it.frameStack
        ballStack = it.ballStack
        frameMax = it.frameMax
        getSharedPrefRules()
        updateFrameInfo()
    }

    fun matchEnded(matchAction: MatchAction) { // When the match ends, reset frame only so you can access the data for the stats screen - temp solution until firebase is created
        if (matchAction == MatchAction.MATCH_END_CONFIRMED_AND_DISCARD_CURRENT_FRAME) resetFrame() else saveAndStartNewFrame() // Discard or save current frame
        sharedPref.setMatchInProgress(false)
    }

    fun saveAndStartNewFrame() = player.apply { // When confirmed from generic dialog or if decided on match end from a generic dialog
        getWinner().addMatchPoint()
        getFirst().frameId = frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        getSecond().frameId = frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        assignPlayers()
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            this@GameViewModel.resetFrame()
            frameCount += 1
            updateFrameInfo()
        }
    }

    fun resetFrame() { // Reset all frame values on match end if chosen to discard current frame, when resetting match , when starting a new frame or on rerack action from the options menu
        frameMax = matchReds * 8 + 27
        player.getFirst().resetFrameScore()
        player.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        ballStack.addBalls(WHITE(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
        repeat(matchReds) { ballStack.addBalls(COLOR(), RED()) }
        assignEventFrameResetComplete() // Once the reset is complete, create a new event to be triggered
        updateFrameInfo()
    }

    // Frame & Match ending queries
    fun queryEndFrame() = assignEventGameAction( // When actioned from options menu or if last ball has been potted
        if (_displayFrame.value!!.isFrameInProgress()) { // If score shows frame in progress, assign a match/frame end QUERY action
            if (player.isWinningTheMatch(matchFrames)) MatchAction.MATCH_END_QUERY
            else MatchAction.FRAME_END_QUERY
        } else { // If score shows frame is over, assign a match/frame end CONFIRMED action
            if (player.isWinningTheMatch(matchFrames)) MatchAction.MATCH_END_CONFIRMED
            else MatchAction.FRAME_END_CONFIRMED
        }
    )

    fun queryEndMatch() = assignEventGameAction( // When actioned from options menu
        when {
            _displayFrame.value!!.isFrameInProgress() -> MatchAction.MATCH_END_QUERY // If score shows frame in progress, assign a match end QUERY
            player.isWinningTheMatch(matchFrames) -> MatchAction.MATCH_END_CONFIRMED // If both frame and match are over assign a match end CONFIRMED action
            else -> MatchAction.MATCH_END_QUERY // If both frame and match aren't over, assign a match end QUERY
        }
    )

    // Handler functions
    fun handleFrameEvent(frameEvent: FrameEvent, pot: DomainPot?, removeRed: Boolean?, freeBall: Boolean?) {
        _isFrameUpdateInProgress.value = true
        when (frameEvent) {
            FrameEvent.HANDLE_FOUL -> handleFoul(pot!!, removeRed!!, freeBall!!)
            FrameEvent.HANDLE_POT -> handleFrameUpdate(pot!!)
            FrameEvent.HANDLE_UNDO -> handleUndo()
        }
        updateFrameInfo()
        _isFrameUpdateInProgress.value = false
        assignEventFrameResetComplete()
    }

    private fun handleFoul(pot: DomainPot, removeRed: Boolean, freeBall: Boolean) {
        handleFrameUpdate(pot)
        if (removeRed) handleFrameUpdate(DomainPot.REMOVERED)
        if (freeBall) frameMax += ballStack.addFreeBall()
    }

    private fun handleFrameUpdate(pot: DomainPot) = ballStack.apply {
        frameStack.addToFrameStack(
            when (pot.potType) {
                in listOf(HIT, FREE, ADDRED) -> DomainPot.HIT(pot.ball)
                FOUL -> DomainPot.FOUL(pot.ball, pot.potAction)
                else -> pot
            },
            player.getPlayerAsInt(),
            frameCount
        )
        player.calculatePoints(pot, 1, this.last(), matchFoul, frameStack)
        when (pot.potType) {
            in listOf(HIT, FREE) -> removeBalls(1)
            in listOf(REMOVERED, ADDRED) -> removeBalls(2)
            else -> {
                if (last() is FREEBALL) {
                    frameStack.addToFrameStack(DomainPot.FREEMISS, player.getPlayerAsInt(), frameCount)
                    frameMax -= removeFreeBall()
                }
                if (last() is COLOR) removeBalls(1)
            }
        }
        if (size == 1) if (player.isFrameEqual()) addBalls(BLACK()) else queryEndFrame() // Query end frame if last ball has been potted
        if (pot.potAction == PotAction.SWITCH) player = player.getOther()
    }

    private fun handleUndo(): Any = ballStack.apply {
        player = player.getPlayerFromInt(frameStack.last().player)
        val lastPot = frameStack.removeFromFrameStack()
        when (lastPot.potType) {
            HIT -> addBalls(if (isNextColor()) COLOR() else lastPot.ball)
            ADDRED -> addBalls(RED(), COLOR())
            FREE -> {
                frameMax += addFreeBall()
                handleUndo()
            }
            REMOVERED -> {
                if (last() is FREEBALL) removeBalls(if (inColors()) 1 else 2)
                if (isNextColor()) addBalls(COLOR(), RED()) else addBalls(RED(), COLOR())
                handleUndo()
            }
            FOUL -> {
                if (last() is FREEBALL) frameMax -= removeFreeBall()
                if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            }
            SAFE -> if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            MISS -> if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
        }
        player.calculatePoints(lastPot, -1, ballStack.last(), matchFoul, frameStack)
    }

    // Helper functions
    private fun assignPlayers() { // With every new frame switch players, else, if match just started, get the player as selected
        if (player.isMatchInProgress()) matchFirst = 1 - matchFirst // Will switch between 0 and 1
        player = player.getPlayerFromInt(matchFirst)
    }

    private fun resetMatch() { // When starting a new match or cancelling an existing match
        getSharedPrefRules()
        player.getFirst().resetMatchScore()
        player.getSecond().resetMatchScore()
        frameCount = 1
        assignPlayers()
        resetFrame()
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
        updateFrameInfo()
    }

    private fun getSharedPrefRules() = sharedPref.apply {
        getApplication<Application>().resources.apply {
            matchFrames = getInt(getString(R.string.shared_pref_match_frames), matchFrames)
            matchReds = getInt(getString(R.string.shared_pref_match_reds), matchReds)
            matchFoul = getInt(getString(R.string.shared_pref_match_foul), matchFoul)
            matchFirst = getInt(getString(R.string.shared_pref_match_first), matchFirst)
            frameCount = getInt(getString(R.string.shared_pref_match_crt_frame), frameCount)
            player = player.getPlayerFromInt(getInt(getString(R.string.shared_pref_match_crt_player), 0))
        }
    }
}