package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainRanking

// IGNORE for now - was part of a world ranking screen. NOT IN USE
//@Entity(tableName = "rankings_table")
//data class DatabaseRanking constructor(
//    @PrimaryKey
//    val position: Int = 0,
//    val name: String = "",
//    val points: Int = 0
//)
//
//fun List<DatabaseRanking>.asDomainRankings(): List<DomainRanking> {
//    return map {
//        DomainRanking(
//            position = it.position,
//            name = it.name,
//            points = it.points
//        )
//    }
//}