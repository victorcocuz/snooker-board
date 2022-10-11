package com.quickpoint.snookerboard.domain

import timber.log.Timber

// The DOMAIN rules
sealed class DomainMatchInfo(
    var frames: Int,
    var reds: Int,
    var foul: Int,
    var first: Int,
    var crtPlayer: Int,
    var frameCount: Int,
    var frameMax: Int
) {
    object RULES : DomainMatchInfo(2, 15, 0, -1, -1, 1, 0) {

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

        fun resetRules() {
            frames = 2
            reds = 15
            foul = 0
            first = -1
            crtPlayer = -1
            frameCount = 1
            frameMax = 0
        }

        fun getRulesText() =
            "Frames: ${frames}, Reds: ${reds}, Foul: ${foul}, First: ${first}, CrtPlayer: ${crtPlayer}, Count: ${frameCount}, Max: ${frames}"

        fun setFrames(number: Int): Int {
            frames = number
            return frames
        }

        fun setReds(number: Int): Int  {
            reds = number
            return reds
        }

        fun setFoul(number: Int) : Int {
            foul = number
            return foul
        }

        fun setFirst(position: Int) : Int {
            first = if (position == 2) (0..1).random() else position
            return first
        }

        fun nominatePlayerAtTable(hasMatchStarted: Boolean) {
            if (hasMatchStarted) first = 1 - first
            crtPlayer = first
        }

        fun switchCrtPlayer() {
            crtPlayer = 1 - crtPlayer
        }

        fun getDisplayFrames() = "(" + (frames * 2 - 1).toString() + ")"
        fun rerackBalls() {
            frameMax = reds * 8 + 27
        }
    }
}