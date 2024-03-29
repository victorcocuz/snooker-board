package com.quickpoint.snookerboard.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.data.database.SnookerDatabase.Companion.TABLE_MATCH_POTS
import com.quickpoint.snookerboard.domain.models.DomainBall
import com.quickpoint.snookerboard.domain.models.DomainBall.*
import com.quickpoint.snookerboard.domain.models.DomainPot
import com.quickpoint.snookerboard.domain.models.DomainPot.*
import com.quickpoint.snookerboard.domain.models.PotAction
import com.quickpoint.snookerboard.domain.models.PotType.*
import com.quickpoint.snookerboard.domain.models.ShotType

@Entity(tableName = TABLE_MATCH_POTS)
data class DbPot(
    @PrimaryKey(autoGenerate = false)
    val potId: Long,
    val breakId: Long,
    val ballId: Long,
    val ballOrdinal: Int,
    val ballPoints: Int,
    val ballFoul: Int,
    val potType: Int,
    val potAction: Int,
    val shotType: Int
)

fun List<DbPot>.asDomain(): MutableList<DomainPot> {
    return map { pot ->
        when (values()[pot.potType]) {
            TYPE_HIT -> HIT(pot.potId, getBallFromValues(pot.ballOrdinal, pot.ballId, pot.ballPoints, pot.ballFoul), ShotType.values()[pot.shotType])
            TYPE_FOUL -> FOUL(pot.potId, getBallFromValues(pot.ballOrdinal, pot.ballId,  pot.ballPoints, pot.ballFoul), PotAction.values()[pot.potAction], ShotType.values()[pot.shotType])
            TYPE_FOUL_ATTEMPT -> FOULATTEMPT(pot.potId, ShotType.values()[pot.shotType])
            TYPE_FREE_ACTIVE -> FREEACTIVE(pot.potId)
            TYPE_FREE -> FREEACTIVE(pot.potId)
            TYPE_SNOOKER -> SNOOKER(pot.potId, ShotType.values()[pot.shotType])
            TYPE_SAFE -> SAFE(pot.potId, ShotType.values()[pot.shotType])
            TYPE_SAFE_MISS -> SAFEMISS(pot.potId, ShotType.values()[pot.shotType])
            TYPE_MISS -> MISS(pot.potId, ShotType.values()[pot.shotType])
            TYPE_REMOVE_RED -> REMOVERED(pot.potId)
            TYPE_REMOVE_COLOR -> REMOVECOLOR(pot.potId)
            TYPE_ADDRED -> ADDRED(pot.potId)
            TYPE_LAST_BLACK_FOULED -> LASTBLACKFOULED(pot.potId)
            TYPE_RESPOT_BLACK -> RESPOTBLACK(pot.potId)
        }
    }.toMutableList()
}

fun getBallFromValues(position: Int, id: Long, points: Int, foul: Int): DomainBall { // Return a DOMAIN ball from a list of values
    return when (position) {
        0 -> NOBALL(id, points, foul)
        1 -> WHITE(id, points, foul)
        2 -> RED(id, points, foul)
        3 -> YELLOW(id, points, foul)
        4 -> GREEN(id, points, foul)
        5 -> BROWN(id, points, foul)
        6 -> BLUE(id, points, foul)
        7 -> PINK(id, points, foul)
        8 -> BLACK(id, points, foul)
        9 -> COLOR(id, points, foul)
        10 -> FREEBALL(id, points, foul)
        11 -> FREEBALLAVAILABLE()
        else -> FREEBALLTOGGLE()
    }
}