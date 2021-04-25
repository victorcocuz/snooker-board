package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.CurrentScore
import com.example.snookerscore.domain.FrameScore

@Entity(tableName = "match_score_table")
data class DatabaseScore(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Int = 0,
    val frameCount: Int,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

@Entity(tableName = "current_score_table")
data class DatabaseCrtScore(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Int = 0,
    val frameCount: Int,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

fun List<DatabaseScore>.asDomainFrameScoreList(): ArrayList<Pair<FrameScore, FrameScore>> {
    val frameScoreList = ArrayList<Pair<FrameScore, FrameScore>>()
    for (i in this.indices step 2) {
        val frameScoreA = FrameScore(
            frameCount = this[i].frameCount,
            playerId = this[i].playerId,
            framePoints = this[i].framePoints,
            matchPoints = this[i].matchPoints,
            successShots = this[i].successShots,
            missedShots = this[i].missedShots,
            fouls = this[i].fouls,
            highestBreak = this[i].highestBreak,
        )
        val frameScoreB = FrameScore(
            frameCount = this[i + 1].frameCount,
            playerId = this[i + 1].playerId,
            framePoints = this[i + 1].framePoints,
            matchPoints = this[i + 1].matchPoints,
            successShots = this[i + 1].successShots,
            missedShots = this[i + 1].missedShots,
            fouls = this[i + 1].fouls,
            highestBreak = this[i + 1].highestBreak,
        )
        frameScoreList.add(Pair(frameScoreA, frameScoreB))
    }
    return frameScoreList
}

fun List<DatabaseCrtScore>.asCurrentScore(): Any? {
    if (this.size > 1) {
        val currentScore: CurrentScore = CurrentScore.PlayerA
        val dbPlayerA = this[this.lastIndex - 1]
        val dbPlayerB = this[this.lastIndex]
        currentScore.getFirst().initPlayer(
            dbPlayerA.frameCount,
            dbPlayerA.framePoints,
            dbPlayerA.matchPoints,
            dbPlayerA.successShots,
            dbPlayerA.missedShots,
            dbPlayerA.fouls,
            dbPlayerA.highestBreak
        )
        currentScore.getSecond().initPlayer(
            dbPlayerB.frameCount,
            dbPlayerB.framePoints,
            dbPlayerB.matchPoints,
            dbPlayerB.successShots,
            dbPlayerB.missedShots,
            dbPlayerB.fouls,
            dbPlayerB.highestBreak
        )
        return currentScore
    }
    return null
}