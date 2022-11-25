package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotType.*

// Used to store pots information in the DATABASE
@Entity(tableName = "match_pots_table")
data class DbPot(
    @PrimaryKey(autoGenerate = false)
    val potId: Long,
    val breakId: Long,
    val ballId: Long,
    val ballOrdinal: Int,
    val ballPoints: Int,
    val ballFoul: Int,
    val potType: Int,
    val potAction: Int
)

// CONVERTER method from list of DATABASE list to list of DOMAIN Pots
fun List<DbPot>.asDomainPotList(): MutableList<DomainPot> {
    return map { pot ->
        when (values()[pot.potType]) {
            TYPE_HIT -> HIT(pot.potId, getBallFromValues(pot.ballOrdinal, pot.ballId, pot.ballPoints, pot.ballFoul))
            TYPE_FOUL -> FOUL(pot.potId, getBallFromValues(pot.ballOrdinal, pot.ballId,  pot.ballPoints, pot.ballFoul), PotAction.values()[pot.potAction])
            TYPE_FREE -> FREETOGGLE(pot.potId)
            TYPE_SAFE -> SAFE(pot.potId)
            TYPE_SAFE_MISS -> SAFEMISS(pot.potId)
            TYPE_SNOOKER -> SNOOKER(pot.potId)
            TYPE_MISS -> MISS(pot.potId)
            TYPE_REMOVE_RED -> REMOVERED(pot.potId)
            TYPE_REMOVE_COLOR -> REMOVECOLOR(pot.potId)
            TYPE_ADDRED -> ADDRED(pot.potId)
            TYPE_FREE_AVAILABLE -> FREEAVAILABLE(pot.potId)
            TYPE_FREE_TOGGLE -> FREETOGGLE(pot.potId)
            TYPE_RESPOT_BLACK -> RESPOTBLACK(pot.potId)
            TYPE_FOUL_ATTEMPT -> FOULATTEMPT(pot.potId)
        }
    }.toMutableList()
}

// CONVERTER method from values to DOMAIN Ball
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