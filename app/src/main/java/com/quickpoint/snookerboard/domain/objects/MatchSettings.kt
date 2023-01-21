package com.quickpoint.snookerboard.domain.objects

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.NONE
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import timber.log.Timber

sealed class MatchSettings(
    var dataStore: DataStore?,
) {
    object Settings : MatchSettings(null) {

        var matchState: MatchState = NONE
            set(value) {
                field = value
                dataStore?.savePreferences(K_LONG_MATCH_STATE, value.ordinal)
                Timber.i("Match state updated: $field")
            }

        var availableFrames: Int = 2
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_FRAMES, value)
            }

        var availableReds: Int = 15
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_REDS, value)
            }

        var foulModifier: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_FOUL_MODIFIER, value)
            }

        var startingPlayer: Int = -1
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_STARTING_PLAYER, value)
            }

        var handicapFrame: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_HANDICAP_FRAME, value)
            }

        var handicapMatch: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_HANDICAP_MATCH, value)
            }

        var crtFrame: Long = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_LONG_MATCH_CRT_FRAME, value)
            }

        var crtPlayer: Int = -1
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_CRT_PLAYER, value)
            }

        var availablePoints: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_AVAILABLE_POINTS, value)
            }

        var counterRetake: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_COUNTER_RETAKE, value)
            }

        var pointsWithoutReturn: Int = 0
            set(value) {
                field = value
                dataStore?.savePreferences(K_INT_MATCH_POINTS_WITHOUT_RETURN, value)
            }

        var uniqueId: Long = 0
            get() {
                field += 1
                dataStore?.savePreferences(K_INT_MATCH_UNIQUE_ID, field)
                return field
            }
            private set

        fun assignUniqueId(): Long {
            uniqueId++
            dataStore?.savePreferences(K_INT_MATCH_UNIQUE_ID, uniqueId)
            return uniqueId
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
            this.matchState = matchState
            this.uniqueId = uniqueId
            this.availableFrames = availableFrames
            this.availableReds = availableReds
            this.foulModifier = foulModifier
            this.startingPlayer = startingPlayer
            this.handicapFrame = handicapFrame
            this.handicapMatch = handicapMatch
            this.crtFrame = crtFrame
            this.crtPlayer = crtPlayer
            this.availablePoints = availablePoints
            this.counterRetake = counterRetake
            this.pointsWithoutReturn = pointsWithoutReturn
            Timber.i("assignRules(): ${getAsText()}")
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
fun Settings.getCrtPlayerFromPotAction(potAction: PotAction) =
    when (potAction) {
        PotAction.SWITCH -> getOtherPlayer()
        PotAction.FIRST -> startingPlayer
        PotAction.CONTINUE, PotAction.RETAKE -> crtPlayer
    }

fun Settings.getDisplayFrames() = "(" + (availableFrames * 2 - 1).toString() + ")"
fun Settings.getAsText() =
    "uniqueId: $uniqueId, availableFrames: $availableFrames, availableReds: $availableReds, foulModifier: $foulModifier, startingPlayer: $startingPlayer, " +
            "handicapFrame: $handicapFrame, handicapMatch: $handicapMatch, crtFrame: $crtFrame, crtPlayer: $crtPlayer, availablePoints: $availablePoints"


