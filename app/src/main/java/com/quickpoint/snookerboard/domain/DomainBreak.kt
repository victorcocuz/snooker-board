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

// Helper methods
fun MutableList<DomainBreak>.isPreviousRed() = this.lastOrNull()?.pots?.lastOrNull()?.ball is DomainBall.RED
fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot, playerAsInt: Int, frameCount: Int) {
    if (pot.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
        || this.size == 0
        || this.last().pots.last().potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
        || this.last().player != playerAsInt
    ) this.add(
        DomainBreak(
            playerAsInt,
            frameCount,
            mutableListOf(),
            0
        )
    )
    this.last().pots.add(pot)
    if (pot.potType in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)) this.last().breakSize += pot.ball.points
}

fun MutableList<DomainBreak>.removeFromFrameStack(): DomainPot {
    val crtPot = this.last().pots.removeLast()
    if (crtPot.potType in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)) this.last().breakSize -= crtPot.ball.points
    if (this.last().pots.size == 0) this.removeLast()
    if (this.size == 1 && this.last().pots.size == 0) this.removeLast()
    return crtPot
}

fun MutableList<DomainBreak>.getDisplayShots(): MutableList<DomainBreak> {
    val list = mutableListOf<DomainBreak>()
    this.forEach {
        if (it.pots.last().potType in listOf(PotType.HIT, PotType.ADDRED, PotType.FREE, PotType.FOUL)
            && it.pots.last().ball != DomainBall.NOBALL()
        ) {
            list.add(it.copy())
        }
    }
    return list
}