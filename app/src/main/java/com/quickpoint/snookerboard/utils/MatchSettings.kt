package com.quickpoint.snookerboard.utils

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

enum class MatchState { RULES_IDLE, RULES_PENDING, GAME_IN_PROGRESS, GAME_SAVED, SUMMARY, NONE }

// The DOMAIN rules
sealed class MatchSettings(
    var matchState: MatchState,
    var uniqueId: Long,
    var maxFramesAvailable: Int,
    var reds: Int,
    var foul: Int,
    var firstPlayer: Int,
    var crtPlayer: Int,
    var crtFrame: Long,
    var maxAvailablePoints: Int,
    var counterRetake: Int,
    var handicapFrame: Int,
    var handicapMatch: Int,
) {
    object SETTINGS : MatchSettings(NONE, 0, 2, 15, 0, -1, -1, 0, 0, 0, 0, 0) {

        // Assign methods
        fun setMatchState(state: MatchState): Int {
            matchState = state
            Timber.i("Match state updated: $matchState")
            return -1
        }

        fun getHandicap(value: Int, pol: Int) = if (pol * value > 0) pol * value else 0

        fun resetRules(): Int {
            uniqueId = -1
            maxFramesAvailable = 2
            reds = 15
            foul = 0
            firstPlayer = -1
            crtPlayer = -1
            crtFrame = 1
            maxAvailablePoints = 0
            counterRetake = 0
            handicapFrame = 0
            handicapMatch = 0
            return -1
        }

        fun assignRules(
            uniqueId: Long,
            maxFramesAvailable: Int,
            reds: Int,
            foul: Int,
            firstPlayer: Int,
            crtPlayer: Int,
            crtFrame: Long,
            maxAvailablePoints: Int,
            retakeCounter: Int,
            handicapFrame: Int,
            handicapMatch: Int,
        ) {
            this.uniqueId = uniqueId
            this.maxFramesAvailable = maxFramesAvailable
            this.reds = reds
            this.foul = foul
            this.firstPlayer = firstPlayer
            this.crtPlayer = crtPlayer
            this.crtFrame = crtFrame
            this.maxAvailablePoints = maxAvailablePoints
            this.counterRetake = retakeCounter
            this.handicapFrame = handicapFrame
            this.handicapMatch = handicapMatch
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
                firstPlayer = 1 - firstPlayer
            }
            maxAvailablePoints = reds * 8 + 27
            crtPlayer = firstPlayer
            counterRetake = 0
        }

        fun setNextPlayerFromPotAction(potAction: PotAction) {
            crtPlayer = when (potAction) {
                SWITCH -> 1 - crtPlayer
                FIRST -> firstPlayer
                CONTINUE, RETAKE -> crtPlayer
            }
        }

        // Text and logs
        fun getAsText() =
            "Frames: $maxFramesAvailable, Reds: $reds, Foul: $foul, First: $firstPlayer, CrtPlayer: $crtPlayer, Count: $crtFrame, Max: $maxFramesAvailable"

        fun getDisplayFrames() = "(" + (maxFramesAvailable * 2 - 1).toString() + ")"
    }
}