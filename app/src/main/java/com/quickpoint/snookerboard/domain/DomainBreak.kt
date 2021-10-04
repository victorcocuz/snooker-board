package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.DbPot

// The DOMAIN Break class is a list of balls potted in one visit (consecutive balls by one player until the other player takes over or the frame ends)
data class DomainBreak(
    val player: Int,
    val frameId: Int,
    val pots: MutableList<DomainPot>,
    var breakSize: Int
)

// CONVERTER method from DOMAIN Break to a list of DATABASE Pots
fun DomainBreak.asDbPot(breakId: Long): List<DbPot> {
    return pots.map { pot ->
        DbPot(
            breakId = breakId,
            ball = pot.ball.getBallOrdinal(),
            ballPoints = pot.ball.points,
            ballFoul = pot.ball.foul,
            potType = pot.potType.ordinal,
            potAction = pot.potAction.ordinal
        )
    }
}