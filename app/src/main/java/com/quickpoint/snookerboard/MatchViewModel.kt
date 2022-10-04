package com.quickpoint.snookerboard

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.*
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
    private val sharedPref: SharedPreferences
) : AndroidViewModel(app) {

    // Variables
    private var player: CurrentPlayer = CurrentPlayer.PLAYER01
    val crtFrame = snookerRepository.crtFrame

    // Observables
    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    private val _displayScore = MutableLiveData<CurrentPlayer>()
    val displayScore: LiveData<CurrentPlayer> = _displayScore
    fun updateFrameInfo(ballStack: MutableList<DomainBall>, frameStack: MutableList<DomainBreak>) {
        player = player.getCrtPlayerFromRules()
        _displayScore.value = player
        _displayFrame.value = DomainFrame(
            RULES.frameCount,
            ballStack,
            mutableListOf(
                player.getFirst().asDomainPlayerScore(),
                player.getSecond().asDomainPlayerScore()
            ),
            frameStack,
            RULES.frameMax
        )
    }

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction
    fun assignEventMatchAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventMatchAction.value = Event(matchAction)
    }

    // Play Fragment actions
    fun startMatchQuery(rules: RULES) {
        Timber.i("startMachQuery(): Is match in progress? ${sharedPref.isMatchInProgress()}")
        if (rules.first < 0) app.toast("Please select who is breaking first")
        else assignEventMatchAction(
            if (sharedPref.isMatchInProgress()) MatchAction.MATCH_START_DIALOG
            else MatchAction.MATCH_START
        )
    }

    fun loadMatchAPointToCrtFrame() { // When actioned from main menu, to load a game
        Timber.i("loadMatchPartAPointToCurrentFrame()")
        viewModelScope.launch {
            snookerRepository.searchByCount(sharedPref.getInt(app.getString(R.string.sp_match_frame_count), 0))
        }
        getSharedPrefRules()
    }

    // Game Fragment actions
    fun loadMatchCDeleteCrtFrame() { // After loading the game, will delete the current frame from the db
        Timber.i("loadMatchPartCDeleteCurrentFrame()")
        viewModelScope.launch {
            snookerRepository.deleteCurrentFrame(sharedPref.getInt(app.getString(R.string.sp_match_frame_count), 0))
        }
    }

    fun startNewMatch() { // When actioned from main menu
        Timber.i("startNewMatch()")
        getSharedPrefRules()
        resetMatch()
        sharedPref.setMatchInProgress(true)
    }

    fun cancelMatch() { // When actioned from options menu
        Timber.i("cancelMatch()")
        RULES.resetRules()
        resetMatch()
        sharedPref.setMatchInProgress(false)
    }

    suspend fun autoSaveMatch() { // TWhen the back button is pressed or when instance state is saved
        Timber.i("saveMatch()")
        if (player.hasMatchStarted()) {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            sharedPref.edit().apply {
                app.resources.apply {
                    putInt(getString(R.string.sp_match_frames), RULES.frames)
                    putInt(getString(R.string.sp_match_reds), RULES.reds)
                    putInt(getString(R.string.sp_match_foul), RULES.foul)
                    putInt(getString(R.string.sp_match_first), RULES.first)
                    putInt(getString(R.string.sp_match_crt_player), player.getPlayerAsInt())
                    putInt(getString(R.string.sp_match_frame_count), RULES.frameCount)
                    putBoolean(getString(R.string.sp_match_freeball_visibility), FREEBALLINFO.isVisible)
                    putBoolean(getString(R.string.sp_match_freeball_selection), FREEBALLINFO.isSelected)
                    apply()
                }
            }
        } else {
            sharedPref.setMatchInProgress(false)
        }
    }

    fun saveAndStartNewFrame() = player.apply { // When confirmed from generic dialog or if decided on match end from a generic dialog
        Timber.i("saveAndStartNewFrame()")
        getWinner().addMatchPoint()
        getFirst().frameId = RULES.frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        getSecond().frameId = RULES.frameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        nominatePlayerAtTable()
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            assignEventMatchAction(MatchAction.FRAME_RESET)
            RULES.frameCount += 1
        }
    }

    // Frame & Match ending
    fun queryEndFrameOrMatch(matchAction: MatchAction): MatchAction { // When actioned from options menu or if last ball has been potted
        Timber.i("queryEndFrameOrMatch()")
        return if (player.isMatchEnding(RULES.frames)) { // If the frame would push player to win, assign a MATCH ending action
            if (displayFrame.value!!.isFrameOver()) MatchAction.MATCH_ENDED
            else MatchAction.MATCH_TO_END_DIALOG
        } else when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
            matchAction == MatchAction.MATCH_ENDED_DIALOG -> MatchAction.MATCH_TO_END_DIALOG
            displayFrame.value!!.isFrameOver() -> MatchAction.FRAME_ENDED
            else -> MatchAction.FRAME_TO_END_DIALOG
        }
    }

    fun matchEnded(matchAction: MatchAction) { // When the match ends, reset frame only so you can access the data for the stats screen - temp solution until firebase is created
        Timber.i("matchEnded()")
        if (matchAction == MatchAction.MATCH_ENDED_DISCARD_FRAME_DIALOG) { // Discard or save current frame
            assignEventMatchAction(MatchAction.FRAME_RESET)
        } else saveAndStartNewFrame() // TEMP - the last frame should be saved, but a new one should not be started
        sharedPref.setMatchInProgress(false)
    }

    // Helpers
    private fun resetMatch() { // When starting a new match or cancelling an existing match
        Timber.i("resetMatch()")
        player.getFirst().resetMatchScore()
        player.getSecond().resetMatchScore()
        nominatePlayerAtTable()
        assignEventMatchAction(MatchAction.FRAME_RESET)
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
    }

    private fun getSharedPrefRules() = sharedPref.apply {
        app.resources.apply {
            RULES.assignRules(
                getInt(getString(R.string.sp_match_frames), 3),
                getInt(getString(R.string.sp_match_reds), 15),
                getInt(getString(R.string.sp_match_foul), 0),
                getInt(getString(R.string.sp_match_first), 0),
                getInt(getString(R.string.sp_match_crt_player), 0),
                getInt(getString(R.string.sp_match_frame_count), 1),
                0
            )
            FREEBALLINFO.assignFreeballInfo(
                getBoolean(getString(R.string.sp_match_freeball_visibility), false),
                getBoolean(getString(R.string.sp_match_freeball_selection), false)
            )
            player = player.getCrtPlayerFromRules()
        }
    }

    private fun nominatePlayerAtTable() { // With every new frame switch players, else, if match just started, get the player as selected
        if (player.hasMatchStarted()) {
            RULES.switchFirst()
        } // Will switch between 0 and 1
        player = player.getFirstPlayerFromRules()
        Timber.i("nominatePlayerAtTable(): Player $player}")
    }
}