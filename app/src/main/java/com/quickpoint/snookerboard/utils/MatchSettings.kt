package com.quickpoint.snookerboard.utils

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.CONTINUE
import com.quickpoint.snookerboard.domain.PotAction.FIRST
import com.quickpoint.snookerboard.domain.PotAction.RETAKE
import com.quickpoint.snookerboard.domain.PotAction.SWITCH
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import com.quickpoint.snookerboard.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.utils.MatchState.GAME_SAVED
import com.quickpoint.snookerboard.utils.MatchState.NONE
import com.quickpoint.snookerboard.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.utils.MatchState.RULES_PENDING
import com.quickpoint.snookerboard.utils.MatchState.SUMMARY
import timber.log.Timber

enum class MatchState { RULES_IDLE, RULES_PENDING, GAME_IN_PROGRESS, GAME_SAVED, SUMMARY, NONE }

// The DOMAIN rules
sealed class MatchSettings(
    var matchState: MatchState,
    var uniqueId: Long,
    var availableFrames: Int,
    var availableReds: Int,
    var foulModifier: Int,
    var startingPlayer: Int,
    var crtPlayer: Int,
    var crtFrame: Long,
    var maxAvailablePoints: Int,
    var counterRetake: Int,
    var handicapFrame: Int,
    var handicapMatch: Int,
    var ongoingPointsWithoutReturn: Int
) {
    object SETTINGS : MatchSettings(NONE, 0, 2, 15, 0, -1, -1, 0, 0, 0, 0, 0,0) {

        // Assign methods
        fun setMatchState(state: MatchState): Int {
            matchState = state
            Timber.i("Match state updated: $matchState")
            return -1
        }

        fun getHandicap(value: Int, pol: Int) = if (pol * value > 0) pol * value else 0

        fun resetRules(): Int {
            uniqueId = -1
            availableFrames = 2
            availableReds = 15
            foulModifier = 0
            startingPlayer = -1
            crtPlayer = -1
            crtFrame = 1
            maxAvailablePoints = 0
            counterRetake = 0
            handicapFrame = 0
            handicapMatch = 0
            ongoingPointsWithoutReturn = 0
            return -1
        }

        fun assignRules(
            uniqueId: Long,
            availableFrames: Int,
            availableReds: Int,
            foulModifier: Int,
            startingPlayer: Int,
            crtPlayer: Int,
            crtFrame: Long,
            maxAvailablePoints: Int,
            retakeCounter: Int,
            handicapFrame: Int,
            handicapMatch: Int,
            ongoingPointsWithoutReturn: Int
        ) {
            this.uniqueId = uniqueId
            this.availableFrames = availableFrames
            this.availableReds = availableReds
            this.foulModifier = foulModifier
            this.startingPlayer = startingPlayer
            this.crtPlayer = crtPlayer
            this.crtFrame = crtFrame
            this.maxAvailablePoints = maxAvailablePoints
            this.counterRetake = retakeCounter
            this.handicapFrame = handicapFrame
            this.handicapMatch = handicapMatch
            this.ongoingPointsWithoutReturn = ongoingPointsWithoutReturn
            Timber.i("assignRules(): ${getAsText()}")
        }

        // Getter methods
        fun getMatchStateFromOrdinal(ordinal: Int) {
            matchState = when (ordinal) {
                0 -> RULES_IDLE
                1 -> RULES_PENDING
                2 -> GAME_IN_PROGRESS
                3 -> GAME_SAVED
                4 -> SUMMARY
                else -> NONE // Always 5
            }
        }

        fun assignUniqueId(): Long {
            uniqueId++
            return uniqueId
        }

        // Helper methods
        fun getOtherPlayer() = 1 - crtPlayer

        fun resetFrameAndGetFirstPlayer(action: MatchAction) {
            if (action == FRAME_START_NEW) {
                crtFrame += 1
                startingPlayer = 1 - startingPlayer
            }
            maxAvailablePoints = availableReds * 8 + 27
            crtPlayer = startingPlayer
            counterRetake = 0
        }

        fun setNextPlayerFromPotAction(potAction: PotAction) {
            crtPlayer = when (potAction) {
                SWITCH -> 1 - crtPlayer
                FIRST -> startingPlayer
                CONTINUE, RETAKE -> crtPlayer
            }
        }

        // Text and logs
        fun getAsText() =
            "Frames: $availableFrames, Reds: $availableReds, Foul: $foulModifier, First: $startingPlayer, CrtPlayer: $crtPlayer, Count: $crtFrame, Max: $availableFrames"

        fun getDisplayFrames() = "(" + (availableFrames * 2 - 1).toString() + ")"
    }
}