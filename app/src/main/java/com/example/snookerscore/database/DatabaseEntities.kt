package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainRanking

@Entity(tableName = "rankings_table")
data class DatabaseRanking constructor(
    @PrimaryKey
    var position: Int = 0,
    val name: String = "",
    val points: Int = 0
)

fun List<DatabaseRanking>.asDomainModel(): List<DomainRanking> {
    return map {
        DomainRanking(
            position = it.position,
            name = it.name,
            points = it.points
        )
    }
}