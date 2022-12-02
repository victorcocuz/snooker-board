package com.quickpoint.snookerboard.utils

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

enum class MatchState { IDLE, IN_PROGRESS, SAVED, POST_MATCH, NONE }

// The DOMAIN rules
sealed class MatchSettings(
    var matchState: MatchState,
    var uniqueId: Long,
    var maxFrames: Int,
    var reds: Int,
    var foul: Int,
    var firstPlayer: Int,
    var crtPlayer: Int,
    var crtFrame: Long,
    var maxAvailable: Int
) {
    object SETTINGS : MatchSettings(IDLE, 0,2, 15, 0, -1, -1, 0, 0) {

        // Assign methods
        fun setMatchState(state: MatchState): Int {
            matchState = state
            Timber.i("Match state updated: $matchState")
            return -1
        }

        fun setFrames(number: Int): Int {
            maxFrames = number
            return maxFrames
        }

        fun setReds(number: Int): Int {
            reds = number
            return reds
        }

        fun setFoul(number: Int): Int {
            foul = number
            return foul
        }

        fun setFirst(position: Int): Int {
            firstPlayer = if (position == 2) (0..1).random() else position
            return firstPlayer
        }

        fun resetRules(): Int {
            uniqueId = -1
            maxFrames = 2
            reds = 15
            foul = 0
            firstPlayer = -1
            crtPlayer = -1
            crtFrame = 1
            maxAvailable = 0
            return -1
        }

        fun assignRules(
            uniqueId: Long,
            frames: Int,
            reds: Int,
            foul: Int,
            first: Int,
            crtPlayer: Int,
            frameCount: Long,
            frameMax: Int
        ) {
            this.uniqueId = uniqueId
            this.maxFrames = frames
            this.reds = reds
            this.foul = foul
            this.firstPlayer = first
            this.crtPlayer = crtPlayer
            this.crtFrame = frameCount
            this.maxAvailable = frameMax
            Timber.i("assignRules(): ${getAsText()}")
        }

        // Getter methods
        fun getMatchStateFromOrdinal(ordinal: Int) = when (ordinal) {
            0 -> this.matchState = IDLE
            1 -> this.matchState = IN_PROGRESS
            2 -> this.matchState = SAVED
            3 -> this.matchState = POST_MATCH
            else -> Timber.e("No match state has been implemented for this ordinal")
        }

        fun assignUniqueId(): Long {
            uniqueId ++
            return uniqueId
        }

        // Helper methods
        fun getOtherPlayer() = 1 - crtPlayer

        fun resetFrameAndGetFirstPlayer(action: MatchAction) {
            if (action == FRAME_START_NEW) {
                crtFrame += 1
                firstPlayer = 1 - firstPlayer
            }
            maxAvailable = reds * 8 + 27
            crtPlayer = firstPlayer
        }

        fun setNextPlayerFromPotAction(potAction: PotAction) {
            crtPlayer = when (potAction) {
                SWITCH -> 1 - crtPlayer
                FIRST -> firstPlayer
                CONTINUE -> crtPlayer
            }
        }

        // Text and logs
        fun getAsText() =
            "Frames: $maxFrames, Reds: $reds, Foul: $foul, First: $firstPlayer, CrtPlayer: $crtPlayer, Count: $crtFrame, Max: $maxFrames"
        fun getDisplayFrames() = "(" + (maxFrames * 2 - 1).toString() + ")"
    }
}