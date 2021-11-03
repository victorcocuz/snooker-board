package com.quickpoint.snookerboard.domain

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
    object RULES : DomainMatchInfo(0, 0, 0, 0, 1, 0, 0)

    fun getCrt() = RULES

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
    }

    fun getRulesText() = "Frames: ${this.frames}, Reds: ${this.reds}, Foul: ${this.foul}, First: ${this.first}, CrtPlayer: ${this.crtPlayer}, Count: ${this.frameCount}, Max: ${this.frames}"

    fun assignMatchFrames(matchFrames: Int) {
        this.frames = matchFrames
    }

    fun assignMatchReds(matchReds: Int) {
        this.reds = matchReds
    }

    fun assignMatchFoul(matchFoul: Int) {
        this.foul = matchFoul
    }

    fun assignMatchFirst(matchFirst: Int) {
        this.first = matchFirst
    }

    fun switchPlayers() {
        this.first = 1 - this.first
    }
}