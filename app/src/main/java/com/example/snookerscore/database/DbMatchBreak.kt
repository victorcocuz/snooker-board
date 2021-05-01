package com.example.snookerscore.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.snookerscore.domain.DomainBreak

@Entity(tableName = "match_breaks_table")
data class DbBreak(
    @PrimaryKey(autoGenerate = false)
    val breakId: Int,
    val player: Int,
    val frameId: Int,
    val breakSize: Int
)

data class DbBreakWithPots(
    @Embedded val matchBreak: DbBreak,
    @Relation(
        parentColumn = "breakId",
        entityColumn = "breakId"
    )
    val matchPots: List<DbPot>
)

fun List<DbBreakWithPots>.asDomainBreakList(): MutableList<DomainBreak> {
    return map {
        DomainBreak(
            player = it.matchBreak.player,
            frameId = it.matchBreak.frameId,
            breakId = it.matchBreak.breakId,
            pots = it.matchPots.asDomainPotList(),
            breakSize = it.matchBreak.breakSize
        )
    }.toMutableList()
}