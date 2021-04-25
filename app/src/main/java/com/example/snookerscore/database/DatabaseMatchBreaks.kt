package com.example.snookerscore.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.snookerscore.domain.*

@Entity(tableName = "match_frame_table")
data class DatabaseFrame(
    @PrimaryKey(autoGenerate = false)
    val frameCount: Int,
)

@Entity(tableName = "match_breaks_table")
data class DatabaseBreak(
    @PrimaryKey(autoGenerate = false)
    val breakId: Int,
    val player: Int,
    val frameCount: Int,
    val breakSize: Int
)

@Entity(tableName = "match_pots_table")
data class DatabasePot(
    @PrimaryKey(autoGenerate = true)
    val potId: Int = 0,
    val breakId: Int,
    val ball: Int,
    val potType: Int,
    val potAction: Int
)

@Entity(tableName = "match_ball_stack_table")
data class DatabaseBall(
    @PrimaryKey(autoGenerate = true)
    val ballId: Int = 0,
    val ballValue: Int
)

data class BreakWithPots(
    @Embedded val matchBreak: DatabaseBreak,
    @Relation(
        parentColumn = "breakId",
        entityColumn = "breakId"
    )
    val matchPots: List<DatabasePot>
)

fun List<BreakWithPots>.asDomainBreakList(): MutableList<Break> {
    return map {
        Break(
            player = it.matchBreak.player,
            frameCount = it.matchBreak.frameCount,
            breakId = it.matchBreak.breakId,
            pots = it.matchPots.asDomainPotList(),
            breakSize = it.matchBreak.breakSize
        )
    }.toMutableList()
}

fun List<DatabasePot>.asDomainPotList(): MutableList<Pot> {
    return map { pot ->
        when (PotType.values()[pot.potType]) {
            PotType.HIT -> Pot.HIT(Ball.values()[pot.ball])
            PotType.FREE -> Pot.FREEMISS
            PotType.SAFE -> Pot.SAFE
            PotType.MISS -> Pot.MISS
            PotType.FOUL -> Pot.FOUL(Ball.values()[pot.ball], PotAction.values()[pot.potAction])
            PotType.REMOVERED -> Pot.REMOVERED
            PotType.ADDRED -> Pot.ADDRED
        }
    }.toMutableList()
}

fun List<DatabaseBall>.asDomainBallStack(): MutableList<Ball> {
    return map {
        (Ball.values()[it.ballValue])
    }.toMutableList()
}