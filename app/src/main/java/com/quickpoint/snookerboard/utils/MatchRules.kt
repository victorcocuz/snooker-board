package com.quickpoint.snookerboard.utils

import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

enum class MatchState { IDLE, IN_PROGRESS, SAVED, POST_MATCH, NONE }

// The DOMAIN rules
sealed class MatchRules(
    var matchState: MatchState,
    var uniqueId: Long,
    var frames: Int,
    var reds: Int,
    var foul: Int,
    var firstPlayer: Int,
    var crtPlayer: Int,
    var frameCount: Long,
    var frameMax: Int
) {
    object RULES : MatchRules(IDLE, 0,2, 15, 0, -1, -1, 0, 0) {

        // Assign methods
        fun setMatchState(state: MatchState): Int {
            matchState = state
            Timber.i("Match state updated: $matchState")
            return -1
        }

        fun setFrames(number: Int): Int {
            frames = number
            return frames
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

        fun resetRules() {
            uniqueId = -1
            frames = 2
            reds = 15
            foul = 0
            firstPlayer = -1
            crtPlayer = -1
            frameCount = 1
            frameMax = 0
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
            this.frames = frames
            this.reds = reds
            this.foul = foul
            this.firstPlayer = first
            this.crtPlayer = crtPlayer
            this.frameCount = frameCount
            this.frameMax = frameMax
            Timber.i("assignRules(): ${getRulesText()}")
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
                frameCount += 1
                firstPlayer = 1 - firstPlayer
            }
            frameMax = reds * 8 + 27
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
        fun getRulesText() =
            "Frames: $frames, Reds: $reds, Foul: $foul, First: $firstPlayer, CrtPlayer: $crtPlayer, Count: $frameCount, Max: $frames"
        fun getDisplayFrames() = "(" + (frames * 2 - 1).toString() + ")"
    }
}