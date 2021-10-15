package com.quickpoint.snookerboard.domain

// The DOMAIN rules
sealed class DomainMatchInfo(
    var matchFrames: Int,
    var matchReds: Int,
    var matchFoul: Int,
    var matchFirst: Int,
    var matchCrtPlayer: Int,
    var matchFrameCount: Int,
    var frameMax: Int
) {
    object RULES : DomainMatchInfo(0, 0, 0, 0, 1, 0, 0)

    fun getCrt() = RULES

    fun assignRules(
        matchFrames: Int,
        matchReds: Int,
        matchFoul: Int,
        matchFirst: Int,
        matchCrtPlayer: Int,
        matchFrameCount: Int,
        frameMax: Int
    ) {
        this.matchFrames = matchFrames
        this.matchReds = matchReds
        this.matchFoul = matchFoul
        this.matchFirst = matchFirst
        this.matchCrtPlayer = matchCrtPlayer
        this.matchFrameCount = matchFrameCount
        this.frameMax = frameMax
    }

    fun assignMatchFrames(matchFrames: Int) {
        this.matchFrames = matchFrames
    }

    fun assignMatchReds(matchReds: Int) {
        this.matchReds = matchReds
    }

    fun assignMatchFoul(matchFoul: Int) {
        this.matchFoul = matchFoul
    }

    fun assignMatchFirst(matchFirst: Int) {
        this.matchFirst = matchFirst
    }

    fun switchPlayers() {
        this.matchFirst = 1 - this.matchFirst
    }
}