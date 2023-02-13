package com.quickpoint.snookerboard.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_BALL
import com.quickpoint.snookerboard.domain.DomainBall

@Entity(tableName = TABLE_MATCH_BALL)
data class DbBall(
    @PrimaryKey(autoGenerate = false)
    val ballId: Long,
    val frameId: Long,
    val ballValue: Int,
    val ballPoints: Int,
    val ballFoul: Int
)

fun List<DbBall>.asDomain(): MutableList<DomainBall> {
    return map {
        (getBallFromValues(it.ballValue, it.ballId, it.ballPoints, it.ballFoul))
    }.toMutableList()
}