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
            PotType.HIT -> DomainPot.HIT(getBallFromValues(pot.ball, pot.ballPoints, pot.ballFoul))
            PotType.FREE -> DomainPot.FREEMISS
            PotType.SAFE -> DomainPot.SAFE
            PotType.MISS -> DomainPot.MISS
            PotType.FOUL -> DomainPot.FOUL(getBallFromValues(pot.ball, pot.ballPoints, pot.ballFoul), PotAction.values()[pot.potAction])
            PotType.REMOVERED -> DomainPot.REMOVERED
            PotType.ADDRED -> DomainPot.ADDRED
        }
    }.toMutableList()
}