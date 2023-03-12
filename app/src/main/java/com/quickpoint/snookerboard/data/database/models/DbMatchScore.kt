package com.quickpoint.snookerboard.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.data.database.SnookerDatabase.Companion.TABLE_MATCH_SCORE
import com.quickpoint.snookerboard.domain.models.DomainScore

@Entity(tableName = TABLE_MATCH_SCORE)
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

fun List<DbScore>.asDomain(): List<DomainScore> {
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

fun List<DbScore>.asDomainPair(): ArrayList<Pair<DomainScore, DomainScore>> {
    val domainScoreList = asDomain()
    val frameScoreList = ArrayList<Pair<DomainScore, DomainScore>>()
    for (i in domainScoreList.indices step 2) {
        frameScoreList.add(Pair(domainScoreList[i], domainScoreList[i + 1]))
    }
    return frameScoreList
}