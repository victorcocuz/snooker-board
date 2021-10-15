package com.quickpoint.snookerboard

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.domain.CurrentPlayer
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainMatchInfo
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.setMatchInProgress
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
    private var rules = DomainMatchInfo.RULES
    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    private val _displayScore = MutableLiveData<CurrentPlayer>()
    val displayScore: LiveData<CurrentPlayer> = _displayScore
    fun updateFrameInfo(player: CurrentPlayer, domainFrame: DomainFrame) {
        _displayScore.value = player
        _displayFrame.value = domainFrame
    }

    private val _eventMatchAction = MutableLiveData<Event<MatchAction>>()
    val eventMatchAction: LiveData<Event<MatchAction>> = _eventMatchAction
    fun assignEventMatchAction(matchAction: MatchAction) { // Such as cancelling a match, ending a frame, etc.
        _eventMatchAction.value = Event(matchAction)
    }

    // Play Fragment actions
    fun startNewMatch(
        nameFirstA: String?,
        nameLastA: String?,
        nameFirstB: String?,
        nameLastB: String?,
        matchFrames: Int,
        matchReds: Int,
        matchFoul: Int,
        matchFirst: Int
    ) { // When actioned from main menu
        Timber.i("START NEW MATCH")
        sharedPref.edit().apply {
            app.resources.apply {
                putString(getString(R.string.sp_match_name_first_a), nameFirstA ?: "")
                putString(getString(R.string.sp_match_name_last_a), nameLastA ?: "")
                putString(getString(R.string.sp_match_name_first_b), nameFirstB ?: "")
                putString(getString(R.string.sp_match_name_last_b), nameLastB ?: "")
                putInt(getString(R.string.sp_match_frames), matchFrames)
                putInt(getString(R.string.sp_match_reds), matchReds)
                putInt(getString(R.string.sp_match_foul), matchFoul)
                putInt(getString(R.string.sp_match_first), matchFirst)
                apply()
            }
        }
        sharedPref.setMatchInProgress(true)
        resetMatch()
    }

    fun loadMatchPartAPointToCurrentFrame() { // When actioned from main menu, to load a game
        Timber.i("LOAD MATCH PART A")
        viewModelScope.launch {
            snookerRepository.searchByCount(sharedPref.getInt(app.resources.getString(R.string.sp_match_frame_count), 0))
        }
        getSharedPrefRules()
    }

    // Game Fragment actions
    fun loadMatchPartCDeleteCurrentFrame() { // After loading the game, will delete the current frame from the db
        Timber.i("LOAD MATCH PART B")
        viewModelScope.launch {
            snookerRepository.deleteCurrentFrame(sharedPref.getInt(app.resources.getString(R.string.sp_match_frame_count), 0))
        }
    }

    fun cancelMatch() { // When actioned from options menu
        Timber.i("CANCEL MATCH")
        sharedPref.setMatchInProgress(false)
        resetMatch()
    }

    suspend fun saveMatch() { // TWhen the back button is pressed or when instance state is saved
        Timber.i("SAVE MATCH")
        if (player.isMatchInProgress()) {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            sharedPref.edit().apply {
                app.resources.apply {
                    putInt(getString(R.string.sp_match_frames), rules.matchFrames)
                    putInt(getString(R.string.sp_match_reds), rules.matchReds)
                    putInt(getString(R.string.sp_match_foul), rules.matchFoul)
                    putInt(getString(R.string.sp_match_first), rules.matchFirst)
                    putInt(getString(R.string.sp_match_crt_player), player.getPlayerAsInt())
                    putInt(getString(R.string.sp_match_frame_count), rules.matchFrameCount)
                    apply()
                }
            }
        } else {
            sharedPref.setMatchInProgress(false)
        }
    }

    fun saveAndStartNewFrame() = player.apply { // When confirmed from generic dialog or if decided on match end from a generic dialog
        Timber.i("SAVE AND START NEW FRAME")
        getWinner().addMatchPoint()
        getFirst().frameId = rules.matchFrameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        getSecond().frameId = rules.matchFrameCount // TEMP - Assign a frameId to later use to add frame info to DATABASE
        assignPlayers()
        viewModelScope.launch {
            snookerRepository.saveCurrentFrame(displayFrame.value!!)
            assignEventMatchAction(MatchAction.FRAME_RESET)
            rules.matchFrameCount += 1
        }
    }

    // Frame & Match ending
    fun queryEndFrame() {
        Timber.i("QUERY END FRAME")
        assignEventMatchAction( // When actioned from options menu or if last ball has been potted
            if (displayFrame.value!!.isFrameInProgress()) { // If score shows frame in progress, assign a match/frame end QUERY action
                if (player.isWinningTheMatch(rules.matchFrames)) MatchAction.MATCH_END_QUERIED
                else MatchAction.FRAME_END_QUERIED
            } else { // If score shows frame is over, assign a match/frame end CONFIRMED action
                if (player.isWinningTheMatch(rules.matchFrames)) MatchAction.MATCH_END_CONFIRMED
                else MatchAction.FRAME_END_CONFIRMED
            }
        )
    }

    fun queryEndMatch() {
        Timber.i("QUERY END MATCH")
        assignEventMatchAction( // When actioned from options menu
            when {
                displayFrame.value!!.isFrameInProgress() -> MatchAction.MATCH_END_QUERIED // If score shows frame in progress, assign a match end QUERY
                player.isWinningTheMatch(rules.matchFrames) -> MatchAction.MATCH_END_CONFIRMED // If both frame and match are over assign a match end CONFIRMED action
                else -> MatchAction.MATCH_END_QUERIED // If both frame and match aren't over, assign a match end QUERY
            }
        )
    }

    fun matchEnded(matchAction: MatchAction) { // When the match ends, reset frame only so you can access the data for the stats screen - temp solution until firebase is created
        Timber.i("MATCH ENDED")
        if (matchAction == MatchAction.MATCH_END_CONFIRM_AND_DISCARD_CRT_FRAME) { // Discard or save current frame
            assignEventMatchAction(MatchAction.FRAME_RESET)
        } else saveAndStartNewFrame() // TEMP - the last frame should be saved, but a new one should not be started
        sharedPref.setMatchInProgress(false)
    }

    // Helpers
    private fun resetMatch() { // When starting a new match or cancelling an existing match
        Timber.i("RESET MATCH")
        player.getFirst().resetMatchScore()
        player.getSecond().resetMatchScore()
        getSharedPrefRules()
        rules.matchFrameCount = 1
        assignPlayers()
        assignEventMatchAction(MatchAction.FRAME_RESET)
        viewModelScope.launch {
            snookerRepository.deleteCurrentMatch()
        }
    }

    private fun getSharedPrefRules() = sharedPref.apply {
        Timber.i("GET SHARED PREF RULES")
        app.resources.apply {
            rules.assignRules(
                getInt(getString(R.string.sp_match_frames), 3),
                getInt(getString(R.string.sp_match_reds), 15),
                getInt(getString(R.string.sp_match_foul), 0),
                getInt(getString(R.string.sp_match_first), 0),
                getInt(getString(R.string.sp_match_crt_player), 0),
                getInt(getString(R.string.sp_match_frame_count), 1),
                0
            )
            player = player.getPlayerFromInt(rules.matchCrtPlayer)
        }
    }

    private fun assignPlayers() { // With every new frame switch players, else, if match just started, get the player as selected
        Timber.i("ASSIGN PLAYERS")
        if (player.isMatchInProgress()) rules.switchPlayers() // Will switch between 0 and 1
        player = player.getPlayerFromInt(rules.matchFirst)
    }
}