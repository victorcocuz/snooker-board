package com.quickpoint.snookerboard.domain.utils

import com.quickpoint.snookerboard.core.utils.MatchAction
import com.quickpoint.snookerboard.core.utils.MatchAction.FRAME_START_NEW
import com.quickpoint.snookerboard.data.*
import com.quickpoint.snookerboard.domain.models.PotAction
import com.quickpoint.snookerboard.domain.models.PotAction.*
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.utils.MatchState.RULES_IDLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class MatchSettings @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    companion object {
        fun getOtherPlayer() = 1 - crtPlayer
        fun handicapFrameExceedsLimit(key: String, value: Int) =
            key == K_INT_MATCH_HANDICAP_FRAME && (handicapFrame + value).absoluteValue >= availableReds * 8 + 27
        fun handicapMatchExceedsLimit(key: String, value: Int) =
            key == K_INT_MATCH_HANDICAP_MATCH && (handicapMatch + value).absoluteValue == availableFrames
        fun getCrtPlayerFromPotAction(potAction: PotAction) = when (potAction) {
            SWITCH -> getOtherPlayer()
            FIRST -> startingPlayer
            CONTINUE, RETAKE -> crtPlayer
        }
        fun getDisplayFrames() = "(" + (availableFrames * 2 - 1).toString() + ")"
        fun resetFrameAndGetFirstPlayer(action: MatchAction) {
            if (action == FRAME_START_NEW) {
                crtFrame += 1
                startingPlayer = 1 - startingPlayer
            }
            maxFramePoints = availableReds * 8 + 27
            crtPlayer = startingPlayer
            counterRetake = 0
        }
        fun updateSettings(key: String, value: Int) {
            when (key) {
                K_INT_MATCH_AVAILABLE_FRAMES -> {
                    availableFrames = value
                    handicapMatch = 0
                }
                K_INT_MATCH_AVAILABLE_REDS -> {
                    availableReds = value
                    handicapFrame = 0
                }
                K_INT_MATCH_FOUL_MODIFIER -> foulModifier = value
                K_INT_MATCH_STARTING_PLAYER -> startingPlayer = if (value == 2) (0..1).random() else value
                K_INT_MATCH_HANDICAP_FRAME -> handicapFrame = if (value == 0) 0 else handicapFrame + value
                K_INT_MATCH_HANDICAP_MATCH -> handicapMatch = if (value == 0) 0 else handicapMatch + value
                else -> Timber.e("Functionality for key $key not implemented")
            }
        }

        fun getAsText() =
            "matchState: $matchState, uniqueId: $uniqueId, availableFrames: $availableFrames, availableReds: $availableReds, foulModifier: $foulModifier, startingPlayer: $startingPlayer, handicapFrame: $handicapFrame, handicapMatch: $handicapMatch, crtFrame: $crtFrame, crtPlayer: $crtPlayer, availablePoints: $maxFramePoints"

        var uniqueId = 0L
            get() {
                field += 1
                return field
            }
            private set
        var foulModifier = 0
        var matchState = RULES_IDLE
        var availableFrames = 2
        var availableReds = 15
        var startingPlayer = -1
        var handicapFrame = 0
        var handicapMatch = 0
        var crtPlayer = -1
        var maxFramePoints = 0
        var counterRetake = 0
        var pointsWithoutReturn = 0
        var crtFrame = 0L
        var isFreeballEnabled = false
    }

    private var _uniqueId = 0L
    private var _foulModifier = 0
    private var _matchState = RULES_IDLE
    private var _availableFrames = 2
    private var _availableReds = 15
    private var _startingPlayer = -1
    private var _handicapFrame = 0
    private var _handicapMatch = 0
    private var _crtPlayer = -1
    private var _maxFramePoints = 0
    private var _counterRetake = 0
    private var _pointsWithoutReturn = 0
    private var _crtFrame = 0L
    private var _isFreeballEnabled = false


    init {
        observeSettings()
    }

    private fun observeSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000) // Check every second
                if (_uniqueId != uniqueId) {
                    _uniqueId = uniqueId
                    dataStoreRepository.savePrefs(K_INT_MATCH_UNIQUE_ID, _uniqueId)
                }
                if (_foulModifier != foulModifier) {
                    _foulModifier = foulModifier
                    dataStoreRepository.savePrefs(K_INT_MATCH_FOUL_MODIFIER, _foulModifier)
                }
                if (_matchState != matchState) {
                    _matchState = matchState
                    dataStoreRepository.savePrefs(K_LONG_MATCH_STATE, _matchState.ordinal)
                    Timber.i("Match state updated: $_matchState")
                }
                if (_availableFrames != availableFrames) {
                    _availableFrames = availableFrames
                    dataStoreRepository.savePrefs(K_INT_MATCH_AVAILABLE_FRAMES, _availableFrames)
                }
                if (_availableReds != availableReds) {
                    _availableReds = availableReds
                    dataStoreRepository.savePrefs(K_INT_MATCH_AVAILABLE_REDS, _availableReds)
                }
                if (_startingPlayer != startingPlayer) {
                    _startingPlayer = startingPlayer
                    dataStoreRepository.savePrefs(K_INT_MATCH_STARTING_PLAYER, _startingPlayer)
                }
                if (_handicapFrame != handicapFrame) {
                    _handicapFrame = handicapFrame
                    dataStoreRepository.savePrefs(K_INT_MATCH_HANDICAP_FRAME, _handicapFrame)
                }
                if (_handicapMatch != handicapMatch){
                    _handicapMatch = handicapMatch
                    dataStoreRepository.savePrefs(K_INT_MATCH_HANDICAP_MATCH, _handicapMatch)
                }
                if (_crtPlayer != crtPlayer) {
                    _crtPlayer = crtPlayer
                    dataStoreRepository.savePrefs(K_INT_MATCH_CRT_PLAYER, _crtPlayer)
                }
                if (_maxFramePoints != maxFramePoints) {
                    _maxFramePoints = maxFramePoints
                    dataStoreRepository.savePrefs(K_INT_MATCH_AVAILABLE_POINTS, _maxFramePoints)
                }
                if (_counterRetake != counterRetake) {
                    _counterRetake = counterRetake
                    dataStoreRepository.savePrefs(K_INT_MATCH_COUNTER_RETAKE, _counterRetake)
                }
                if (_pointsWithoutReturn != pointsWithoutReturn) {
                    _pointsWithoutReturn = pointsWithoutReturn
                    dataStoreRepository.savePrefs(K_INT_MATCH_POINTS_WITHOUT_RETURN, _pointsWithoutReturn)
                }
                if (_crtFrame != crtFrame) {
                    _crtFrame = crtFrame
                    dataStoreRepository.savePrefs(K_LONG_MATCH_CRT_FRAME, _crtFrame)
                }
                if (_isFreeballEnabled != isFreeballEnabled) {
                    _isFreeballEnabled = isFreeballEnabled
                    dataStoreRepository.savePrefs(K_BOOL_TOGGLE_FREEBALL, _isFreeballEnabled)
                }
            }
        }
    }

    fun resetRules(): Int {
        uniqueId = -1
        availableFrames = 2
        availableReds = 15
        foulModifier = 0
        startingPlayer = -1
        handicapFrame = 0
        handicapMatch = 0
        crtFrame = 1L
        crtPlayer = -1
        maxFramePoints = 0
        counterRetake = 0
        pointsWithoutReturn = 0
        return -1
    }

    fun loadPreferences(
        matchState: MatchState,
        uniqueId: Long,
        availableFrames: Int,
        availableReds: Int,
        foulModifier: Int,
        startingPlayer: Int,
        handicapFrame: Int,
        handicapMatch: Int,
        crtFrame: Long,
        crtPlayer: Int,
        maxFramePoints: Int,
        counterRetake: Int,
        pointsWithoutReturn: Int,
    ) {
        this._matchState = matchState
        this._uniqueId = uniqueId
        this._availableFrames = availableFrames
        this._availableReds = availableReds
        this._foulModifier = foulModifier
        this._startingPlayer = startingPlayer
        this._handicapFrame = handicapFrame
        this._handicapMatch = handicapMatch
        this._crtFrame = crtFrame
        this._crtPlayer = crtPlayer
        this._maxFramePoints = maxFramePoints
        this._counterRetake = counterRetake
        this._pointsWithoutReturn = pointsWithoutReturn
        Timber.i("loadPreferences(): ${getAsText()}")
    }
}
