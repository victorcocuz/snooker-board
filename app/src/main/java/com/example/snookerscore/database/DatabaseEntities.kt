package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.fragments.game.Frame
import com.example.snookerscore.fragments.game.FrameScore

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

@Entity(tableName = "frames_table")
data class DatabaseFrame constructor(
    @PrimaryKey
    val frameCount: Int = 0,
    val frameScore: List<FrameScore>
)

fun List<DatabaseFrame>.asDomainFrames(): List<Frame> {
    return map {
        Frame(
            frameCount = it.frameCount,
            frameScore = it.frameScore
        )
    }
}

//@Entity(tableName = "current_match_table")
//data class DatabaseCrtMatch constructor(
//    @PrimaryKey
//    val crtMatch: Int = 0,
//    val matchFrames: Int,
//    val matchReds: Int,
//    val matchFoulModifier: Int,
//    val matchBreaksFirst: Int,
//    val frameCount: Int,
//    val ballStack: ArrayDeque<Ball>,
//    val frameScore: CurrentFrame,
//    val frameStack: ArrayDeque<Break>
//)