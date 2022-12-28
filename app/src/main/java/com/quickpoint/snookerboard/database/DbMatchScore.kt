package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainScore

// Used to store score info in the Database
@Entity(tableName = "match_score_table")
data class DbScore(
    @PrimaryKey(autoGenerate = false)
    val scoreId: Long,
    val frameId: Long,
    val playerId: Int,
    val framePoints: Int,
    val matchPoints: Int,
    val successShots: Int,
    val missedShots: Int,
    val safetySuccessShots: Int,
    val safetyMissedShots: Int,
    val snookers: Int,
    val fouls: Int,
    val highestBreak: Int,
    val longShotsSuccess: Int,
    val longShotsMissed: Int,
    val restShotsSuccess: Int,
    val restShotsMissed: Int,
    val pointsWithNoReturn: Int
)

// CONVERTER method from list of DATABASE Break to list of DOMAIN Player Score
fun List<DbScore>.asDomainScoreList(): MutableList<DomainScore> {
    return map {
        DomainScore(
            scoreId = it.scoreId,
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
            longShotsSuccess = it.longShotsSuccess,
            longShotsMissed = it.longShotsMissed,
            restShotsSuccess = it.restShotsSuccess,
            restShotsMissed = it.restShotsMissed,
            pointsWithoutReturn = it.pointsWithNoReturn
        )
    }.toMutableList()
}

// CONVERTER method from list of DATABASE Score to a list of pairs of DOMAIN Player Scores
fun List<DbScore>.asDomainFrameScoreList(): ArrayList<Pair<DomainScore, DomainScore>> {
    val domainScoreList = this.asDomainScoreList()
    val frameScoreList = ArrayList<Pair<DomainScore, DomainScore>>()
    for (i in domainScoreList.indices step 2) {
        frameScoreList.add(Pair(domainScoreList[i], domainScoreList[i + 1]))
    }
    return frameScoreList
}