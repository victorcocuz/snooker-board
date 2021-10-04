package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.getBallFromValues

// Used to store the ball stack within the DATABASE
@Entity(tableName = "match_ball_stack_table")
data class DbBall(
    @PrimaryKey(autoGenerate = true)
    val ballId: Int = 0,
    val frameId: Int,
    val ballValue: Int,
    val ballPoints: Int,
    val ballFoul: Int
)

// CONVERTER method from DATABASE Ball to DOMAIN Ball
fun List<DbBall>.asDomainBallStack(): MutableList<DomainBall> {
    return map {
        (getBallFromValues(it.ballValue, it.ballPoints, it.ballFoul))
    }.toMutableList()
}