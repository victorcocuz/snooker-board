package com.quickpoint.snookerboard.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.quickpoint.snookerboard.domain.DomainFrame

// Used to store frames in the DATABASE
@Entity(tableName = "match_frames_table")
data class DbFrame(
    @PrimaryKey(autoGenerate = false)
    val frameId: Int,
    var frameMax: Int
)

// Used to store all frame info within the DATABASE
data class DbFrameWithScoreAndBreakWithPotsAndBallStack(
    @Embedded val frame: DbFrame,
    @Relation(
        parentColumn = "frameId",
        entityColumn = "frameId",
    )
    val frameScore: List<DbScore>,
    @Relation(
        parentColumn = "frameId",
        entityColumn = "frameId",
        entity = DbBreak::class
    )
    val frameStack: List<DbBreakWithPots>,
    @Relation(
        parentColumn = "frameId",
        entityColumn = "frameId"
    )
    val ballStack: List<DbBall>
)

// CONVERTER method from a DATABASE Frame to DOMAIN Frame
fun DbFrameWithScoreAndBreakWithPotsAndBallStack.asDomainFrame(): DomainFrame {
    return DomainFrame(
        frameId = this.frame.frameId,
        frameScore = this.frameScore.asDomainPlayerScoreList(),
        frameStack = this.frameStack.asDomainBreakList(),
        ballStack = this.ballStack.asDomainBallStack(),
        frameMax = this.frame.frameMax
    )
}