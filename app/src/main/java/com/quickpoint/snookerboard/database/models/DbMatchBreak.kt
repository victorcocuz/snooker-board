package com.quickpoint.snookerboard.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.quickpoint.snookerboard.database.SnookerDatabase.Companion.TABLE_MATCH_BREAKS
import com.quickpoint.snookerboard.domain.DomainBreak

@Entity(tableName = TABLE_MATCH_BREAKS)
data class DbBreak(
    @PrimaryKey(autoGenerate = false)
    val breakId: Long,
    val player: Int,
    val frameId: Long,
    val breakSize: Int,
    val pointsWithoutReturn: Int
)

// Used to combine Break and Pots information for DATABASE storage
data class DbBreakWithPots(
    @Embedded val matchBreak: DbBreak,
    @Relation(
        parentColumn = "breakId",
        entityColumn = "breakId"
    )
    val matchPots: List<DbPot>
)

fun List<DbBreakWithPots>.asDomain(): MutableList<DomainBreak> {
    return map {
        DomainBreak(
            breakId = it.matchBreak.breakId,
            player = it.matchBreak.player,
            frameId = it.matchBreak.frameId,
            pots = it.matchPots.asDomain(),
            breakSize = it.matchBreak.breakSize,
            pointsWithoutReturn = it.matchBreak.pointsWithoutReturn
        )
    }.toMutableList()
}