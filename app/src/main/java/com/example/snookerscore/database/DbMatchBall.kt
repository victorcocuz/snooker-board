package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainBall
import com.example.snookerscore.domain.getBallFromValues

@Entity(tableName = "match_ball_stack_table")
data class DbBall(
    @PrimaryKey(autoGenerate = true)
    val ballId: Int = 0,
    val frameId: Int,
    val ballValue: Int,
    val ballPoints: Int,
    val ballFoul: Int
)

fun List<DbBall>.asDomainBallStack(): MutableList<DomainBall> {
    return map {
        (getBallFromValues(it.ballValue, it.ballPoints, it.ballFoul))
    }.toMutableList()
}