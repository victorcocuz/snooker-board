package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainPlayerScore

// Used to store score info in the Database
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
    val safetySuccessShots: Int,
    val safetyMissedShots: Int,
    val snookers: Int,
    val fouls: Int,
    val highestBreak: Int
)

// CONVERTER method from list of DATABASE Score to a list of pairs of DOMAIN Player Scores
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
            safetySuccessShots = this[i].safetySuccessShots,
            safetyMissedShots = this[i].safetyMissedShots,
            snookers = this[i].snookers,
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
            safetySuccessShots = this[i].safetySuccessShots,
            safetyMissedShots = this[i].safetyMissedShots,
            snookers = this[i].snookers,
            fouls = this[i + 1].fouls,
            highestBreak = this[i + 1].highestBreak,
        )
        frameScoreList.add(Pair(frameScoreA, frameScoreB))
    }
    return frameScoreList
}

// CONVERTER method from list of DATABASE Break to list of DOMAIN Player Score
fun List<DbScore>.asDomainPlayerScoreList(): MutableList<DomainPlayerScore> {
    return map {
        DomainPlayerScore(
            frameId = it.frameId,
            playerId = it.playerId,
            framePoints = it.framePoints,
            matchPoints = it.matchPoints,
            successShots = it.successShots,
            missedShots = it.missedShots,
            safetySuccessShots = it.safetySuccessShots,
            safetyMissedShots = it.safetyMissedShots,
            snookers = it.snookers,
            fouls = it.fouls,
            highestBreak = it.highestBreak,
        )
    }.toMutableList()
}