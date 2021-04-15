package com.example.snookerscore.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.fragments.game.Frame
import com.example.snookerscore.fragments.game.FrameScore

@Entity(tableName = "rankings_table")
data class DatabaseRanking constructor(
    @PrimaryKey
    var position: Int = 0,
    val name: String = "",
    val points: Int = 0
)

@Entity(tableName = "frames_table")
data class DatabaseFrame constructor(
    @PrimaryKey
    var frameCount: Int = 0,

    @Embedded
    var frameScore: FrameScore
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

fun List<DatabaseFrame>.asDomainFrames(): List<Frame> {
    return map {
        Frame(
            frameCount = it.frameCount,
            frameScore = it.frameScore
        )
    }
}