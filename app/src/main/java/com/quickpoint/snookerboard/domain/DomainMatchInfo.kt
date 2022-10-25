package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.MatchState.*
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_RERACK
import timber.log.Timber

enum class MatchState { IDLE, IN_PROGRESS, SAVED, POST_MATCH }

// The DOMAIN rules
sealed class DomainMatchInfo(
    var matchState: MatchState,
    var frames: Int,
    var reds: Int,
    var foul: Int,
    var first: Int,
    var crtPlayer: Int,
    var frameCount: Int,
    var frameMax: Int
) {
    object RULES : DomainMatchInfo(IDLE, 2, 15, 0, -1, -1, 0, 0) {

        fun assignRules(
            frames: Int,
            reds: Int,
            foul: Int,
            first: Int,
            crtPlayer: Int,
            frameCount: Int,
            frameMax: Int
        ) {
            this.frames = frames
            this.reds = reds
            this.foul = foul
            this.first = first
            this.crtPlayer = crtPlayer
            this.frameCount = frameCount
            this.frameMax = frameMax
            Timber.i("assignRules(): ${getRulesText()}")
        }

        fun getMatchStateFromOrdinal(ordinal: Int) = when (ordinal) {
            0 -> this.matchState = IDLE
            1 -> this.matchState = IN_PROGRESS
            2 -> this.matchState = SAVED
            3 -> this.matchState = POST_MATCH
            else -> Timber.e("No match state has been implemented for this ordinal")
        }

        fun resetRules() {
            matchState = IDLE
            frames = 2
            reds = 15
            foul = 0
            first = -1
            crtPlayer = -1
            frameCount = 1
            frameMax = 0
        }

        fun getRulesText() =
            "Frames: $frames, Reds: $reds, Foul: $foul, First: $first, CrtPlayer: $crtPlayer, Count: $frameCount, Max: $frames"

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
            first = if (position == 2) (0..1).random() else position
            return first
        }

        fun getDisplayFrames() = "(" + (frames * 2 - 1).toString() + ")"

        fun resetFrameAndGetFirstPlayer(matchAction: MatchAction) {
            if (matchAction == MatchAction.FRAME_START_NEW) frameCount += 1
            if (matchAction != FRAME_RERACK && frameCount > 1) first = 1 - first
            frameMax = reds * 8 + 27
            crtPlayer = first
        }

        fun setNextPlayerFromPotAction(potAction: PotAction) {
            crtPlayer = when (potAction) {
                SWITCH -> 1 - crtPlayer
                FIRST -> first
                CONTINUE -> crtPlayer
            }
        }
    }
}