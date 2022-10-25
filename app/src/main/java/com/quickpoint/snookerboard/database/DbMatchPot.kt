package com.quickpoint.snookerboard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.getBallFromValues

// Used to store pots information in the DATABASE
@Entity(tableName = "match_pots_table")
data class DbPot(
    @PrimaryKey(autoGenerate = true)
    val potId: Int = 0,
    val breakId: Long,
    val ball: Int,
    val ballPoints: Int,
    val ballFoul: Int,
    val potType: Int,
    val potAction: Int
)

// CONVERTER method from list of DATABASE list to list of DOMAIN Pots
fun List<DbPot>.asDomainPotList(): MutableList<DomainPot> {
    return map { pot ->
        when (PotType.values()[pot.potType]) {
            PotType.TYPE_HIT -> DomainPot.HIT(getBallFromValues(pot.ball, pot.ballPoints, pot.ballFoul))
            PotType.TYPE_FREE -> DomainPot.FREETOGGLE
            PotType.TYPE_SAFE -> DomainPot.SAFE
            PotType.TYPE_MISS -> DomainPot.MISS
            PotType.TYPE_FOUL -> DomainPot.FOUL(getBallFromValues(pot.ball, pot.ballPoints, pot.ballFoul), PotAction.values()[pot.potAction])
            PotType.TYPE_REMOVERED -> DomainPot.REMOVERED
            PotType.TYPE_ADDRED -> DomainPot.ADDRED
            PotType.TYPE_FREEAVAILABLE -> DomainPot.FREEAVAILABLE
            PotType.TYPE_FREETOGGLE -> DomainPot.FREETOGGLE
            PotType.TYPE_RESPOT_BLACK -> DomainPot.RESPOTBLACK
        }
    }.toMutableList()
}