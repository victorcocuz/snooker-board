package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainPlayerScore

@Entity(tableName = "match_score_table")
data class DbScore(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Int = 0,
    val frameId: Int,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val fouls: Int,
    val highestBreak: Int
)

//@Entity(tableName = "current_score_table")
//data class DbScore(
//    @PrimaryKey(autoGenerate = true)
//    val scoreId: Int = 0,
//    val frameId: Int,
//    val playerId: Int,
//    val framePoints: Int,
//    val matchPoints: Int,
//    val successShots: Int,
//    val missedShots: Int,
//    val fouls: Int,
//    val highestBreak: Int
//)

fun List<DbScore>.asDomainFrameScoreList(): ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>> {
    val frameScoreList = ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>()
    for (i in this.indices step 2) {
        val frameScoreA = DomainPlayerScore(
            frameId = this[i].frameId,
            playerId = this[i].playerId,
            framePoints = this[i].framePoints,
            matchPoints = this[i].matchPoints,
            successShots = this[i].successShots,
            missedShots = this[i].missedShots,
            fouls = this[i].fouls,
            highestBreak = this[i].highestBreak,
        )
        val frameScoreB = DomainPlayerScore(
            frameId = this[i + 1].frameId,
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

fun List<DbScore>.asDomainCrtFrameScoreList(): MutableList<DomainPlayerScore> {
    return map {
        DomainPlayerScore(
            frameId = it.frameId,
            playerId = it.playerId,
            framePoints = it.framePoints,
            matchPoints = it.matchPoints,
            successShots = it.successShots,
            missedShots = it.missedShots,
            fouls = it.fouls,
            highestBreak = it.highestBreak,
        )
    }.toMutableList()
}