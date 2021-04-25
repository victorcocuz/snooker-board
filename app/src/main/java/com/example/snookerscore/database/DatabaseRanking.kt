package com.example.snookerscore.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.snookerscore.domain.*

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