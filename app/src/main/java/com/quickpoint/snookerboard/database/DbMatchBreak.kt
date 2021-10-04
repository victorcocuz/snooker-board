package com.quickpoint.snookerboard.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.quickpoint.snookerboard.domain.DomainBreak

// Used to store the match breaks in the DATABASE
@Entity(tableName = "match_breaks_table")
data class DbBreak(
    @PrimaryKey(autoGenerate = true)
    val breakId: Long = 0,
    val player: Int,
    val frameId: Int,
    val breakSize: Int
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

// CONVERTER method from list of DATABASE Break to list of DOMAIN Break
fun List<DbBreakWithPots>.asDomainBreakList(): MutableList<DomainBreak> {
    return map {
        DomainBreak(
            player = it.matchBreak.player,
            frameId = it.matchBreak.frameId,
            pots = it.matchPots.asDomainPotList(),
            breakSize = it.matchBreak.breakSize
        )
    }.toMutableList()
}