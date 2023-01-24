package com.quickpoint.snookerboard.domain.objects

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.NONE
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import timber.log.Timber
import kotlin.math.absoluteValue

sealed class MatchSettings(
    var dataStore: DataStore?,
) {
    object Settings : MatchSettings(null) {

        private var mMatchState: MatchState = NONE
        var matchState: MatchState
            get() = mMatchState
            set(value) {
                mMatchState = value
                dataStore?.savePreferences(K_LONG_MATCH_STATE, value.ordinal)
                Timber.i("Match state updated: $mMatchState")
            }

        private var mUniqueId: Long = 0
        var uniqueId: Long = 0
            get() {
                mUniqueId += 1
                dataStore?.savePreferences(K_INT_MATCH_UNIQUE_ID, mUniqueId)
                return mUniqueId
            }
            private set

        fun assignUniqueId(): Long {
            uniqueId++
            dataStore?.savePreferences(K_INT_MATCH_UNIQUE_ID, uniqueId)
            return uniqueId
        }

        private var mAvailableFrames: Int = 2
        var availableFrames: Int
            get() = mAvailableFrames
            set(value) {
                mAvailableFrames = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_FRAMES, value)
            }

        private var mAvailableReds: Int = 15
        var availableReds: Int
            get() = mAvailableReds
            set(value) {
                mAvailableReds = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_REDS, value)
            }

        private var mFoulModifier: Int = 0
        var foulModifier: Int
            get() = mFoulModifier
            set(value) {
                mFoulModifier = value
                dataStore?.savePreferences(K_INT_MATCH_FOUL_MODIFIER, value)
            }

        private var mStartingPlayer: Int = -1
        var startingPlayer: Int
            get() = mStartingPlayer
            set(value) {
                mStartingPlayer = value
                dataStore?.savePreferences(K_INT_MATCH_STARTING_PLAYER, value)
            }

        private var mHandicapFrame: Int = 0
        var handicapFrame: Int
            get() = mHandicapFrame
            set(value) {
                mHandicapFrame = value
                dataStore?.savePreferences(K_INT_MATCH_HANDICAP_FRAME, value)
            }

        private var mHandicapMatch: Int = 0
        var handicapMatch: Int
            get() = mHandicapMatch
            set(value) {
                mHandicapMatch = value
                dataStore?.savePreferences(K_INT_MATCH_HANDICAP_MATCH, value)
            }

        private var mCrtFrame: Long = 0
        var crtFrame: Long
            get() = mCrtFrame
            set(value) {
                mCrtFrame = value
                dataStore?.savePreferences(K_LONG_MATCH_CRT_FRAME, value)
            }

        private var mCrtPlayer: Int = -1
        var crtPlayer: Int
            get() = mCrtPlayer
            set(value) {
                mCrtPlayer = value
                dataStore?.savePreferences(K_INT_MATCH_CRT_PLAYER, value)
            }

        private var mAvailablePoints: Int = 0
        var availablePoints: Int
            get() = mAvailablePoints
            set(value) {
                mAvailablePoints = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_POINTS, value)
            }

        private var mCounterRetake: Int = 0
        var counterRetake: Int
            get() = mCounterRetake
            set(value) {
                mCounterRetake = value
                dataStore?.savePreferences(K_INT_MATCH_COUNTER_RETAKE, value)
            }

        private var mPointsWithoutReturn: Int = 0
        var pointsWithoutReturn: Int
            get() = mPointsWithoutReturn
            set(value) {
                mPointsWithoutReturn = value
                dataStore?.savePreferences(K_INT_MATCH_POINTS_WITHOUT_RETURN, value)
            }

        fun resetRules(): Int {
            uniqueId = -1
            availableFrames = 2
            availableReds = 15
            foulModifier = 0
            startingPlayer = -1
            handicapFrame = 0
            handicapMatch = 0
            crtFrame = 1
            crtPlayer = -1
            availablePoints = 0
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
            availablePoints: Int,
            counterRetake: Int,
            pointsWithoutReturn: Int,
        ) {
            this.mMatchState = matchState
            this.mUniqueId = uniqueId
            this.mAvailableFrames = availableFrames
            this.mAvailableReds = availableReds
            this.mFoulModifier = foulModifier
            this.mStartingPlayer = startingPlayer
            this.mHandicapFrame = handicapFrame
            this.mHandicapMatch = handicapMatch
            this.mCrtFrame = crtFrame
            this.mCrtPlayer = crtPlayer
            this.mAvailablePoints = availablePoints
            this.mCounterRetake = counterRetake
            this.mPointsWithoutReturn = pointsWithoutReturn
            Timber.i("loadPreferences(): ${getAsText()}")
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

        fun resetFrameAndGetFirstPlayer(action: MatchAction) {
            if (action == FRAME_START_NEW) {
                crtFrame += 1
                startingPlayer = 1 - startingPlayer
            }
            availablePoints = availableReds * 8 + 27
            crtPlayer = startingPlayer
            counterRetake = 0
        }
    }
}

// Helper methods
fun Settings.getOtherPlayer() = 1 - crtPlayer
fun Settings.handicapFrameExceedsLimit(key: String, value: Int) = key == K_INT_MATCH_HANDICAP_FRAME && (handicapFrame + value).absoluteValue >= availableReds * 8 + 27
fun Settings.handicapMatchExceedsLimit(key: String, value: Int) = key == K_INT_MATCH_HANDICAP_MATCH && (handicapMatch + value).absoluteValue == availableFrames
fun Settings.getCrtPlayerFromPotAction(potAction: PotAction) = when (potAction) {
        SWITCH -> getOtherPlayer()
        FIRST -> startingPlayer
        CONTINUE, RETAKE -> crtPlayer
}

fun Settings.getDisplayFrames() = "(" + (availableFrames * 2 - 1).toString() + ")"
fun Settings.getAsText() =
    "matchState: $matchState, uniqueId: $uniqueId, availableFrames: $availableFrames, availableReds: $availableReds, foulModifier: $foulModifier, startingPlayer: $startingPlayer, " +
            "handicapFrame: $handicapFrame, handicapMatch: $handicapMatch, crtFrame: $crtFrame, crtPlayer: $crtPlayer, availablePoints: $availablePoints"


