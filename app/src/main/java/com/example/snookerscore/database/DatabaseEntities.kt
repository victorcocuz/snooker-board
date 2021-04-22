package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.domain.FrameScore

@Entity(tableName = "rankings_table")
data class DatabaseRanking constructor(
    @PrimaryKey
    val position: Int = 0,
    val name: String = "",
    val points: Int = 0
)

fun List<DatabaseRanking>.asDomainRankings(): List<DomainRanking> {
    return map {
        DomainRanking(
            position = it.position,
            name = it.name,
            points = it.points
        )
    }
}

@Entity(tableName = "frames_table")
data class DatabaseFrameScore constructor(
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

fun List<DatabaseFrameScore>.asDomainFrameScoreList(): ArrayList<Pair<FrameScore, FrameScore>> {
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

@Entity(tableName = "crt_break_table")
data class DatabaseMatchBreak constructor(
    @PrimaryKey(autoGenerate = true)
    val breakId: Int = 0,
    val player : Int,
    val potsBreakId: Int,
    val breakSize: Int
    )

@Entity(tableName = "crt_pot_table")
data class DatabaseMatchPot constructor(
    @PrimaryKey(autoGenerate = true)
    val potId: Int = 0,
    val potsBreakId: Int,
    val ball: Int,
    val potType: Int,
    val potAction: Int
)

//fun List<DatabaseMatchPot>.asDomainPotList(): ArrayDeque<Pot> {
//    return map { pot ->
//        when(pot.potType) {
//            PotType.HIT -> Pot.HIT(pot.ball)
//        }
//    }.toMutableList().toTypedArray()
//}