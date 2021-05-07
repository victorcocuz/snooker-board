package com.example.snookerscore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snookerscore.domain.DomainPot
import com.example.snookerscore.domain.PotAction
import com.example.snookerscore.domain.PotType
import com.example.snookerscore.domain.getBallFromValues

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