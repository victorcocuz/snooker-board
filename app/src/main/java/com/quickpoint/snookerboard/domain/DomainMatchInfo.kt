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

        private fun getRulesText() =
            "Frames: ${frames}, Reds: ${reds}, Foul: ${foul}, First: ${first}, CrtPlayer: ${crtPlayer}, Count: ${frameCount}, Max: ${frames}"

        fun updateFrames(number: Int) : RULES {
            frames = number
            return this
        }

        fun updateReds(number: Int) : RULES {
            reds = number
            return this
        }

        fun updateFoul(number: Int) : RULES {
            foul = number
            return this
        }

        fun updateFirst(position: Int) : RULES {
            first = if (position == 2) (0..1).random() else position
            return this
        }

        fun switchFirst() {
            first = 1 - first
        }

        fun switchCrtPlayer() {
            crtPlayer = 1 - crtPlayer
        }
    }
}