package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.database.DbPot
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.PotType.*

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
fun MutableList<DomainBreak>.lastPotType() = lastOrNull()?.pots?.last()?.potType
fun DomainBreak.lastPotType() = pots.lastOrNull()?.potType
fun MutableList<DomainBreak>.lastBallType() = lastOrNull()?.pots?.last()?.ball?.ballType
fun DomainBreak.lastBall() = pots.lastOrNull()?.ball

fun MutableList<DomainBreak>.handlePot(pot: DomainPot) { // Add to frameStack all pots, but remove repeated freeball toggles
    if (pot.potType == TYPE_FREETOGGLE && lastPotType() == TYPE_FREETOGGLE) removeLastPotFromFrameStack()
    else addToFrameStack(pot)
}
fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot) {
    if (pot.potType !in listOf(TYPE_HIT, TYPE_FREE, TYPE_ADDRED) // If the current pot is not generating points
        || size == 0 // or if there are no breaks
        || lastPotType() !in listOf(TYPE_HIT, TYPE_FREE, TYPE_ADDRED) // or if previous pot was not generating points
        || last().player != RULES.crtPlayer // or if player has changed
    ) add(DomainBreak(RULES.crtPlayer, RULES.frameCount, mutableListOf(), 0)) // Add a new current break
    last().pots.add( // Add the current pot to the current break
        when (pot.potType) {
            TYPE_HIT -> DomainPot.HIT(pot.ball)
            TYPE_FOUL -> DomainPot.FOUL(pot.ball, pot.potAction)
            else -> pot
        }
    ) // Add the pot to the current break
    if (pot.potType in listOf(TYPE_HIT, TYPE_FREE, TYPE_ADDRED)) last().breakSize += pot.ball.points // Update the current break size
}

fun MutableList<DomainBreak>.findMaxBreak(): Int {
    var highestBreak = 0
    this.forEach { crtBreak ->
        if (RULES.crtPlayer == crtBreak.player && crtBreak.breakSize > highestBreak) {
            highestBreak = crtBreak.breakSize
        }
    }
    return highestBreak
}

fun MutableList<DomainBreak>.removeLastPotFromFrameStack(): DomainPot {
    val pot = last().pots.removeLast() // Get the last pot
    if (pot.potType in listOf(TYPE_HIT, TYPE_FREE, TYPE_ADDRED)) last().breakSize -= pot.ball.points // Update current break size
    while (size > 0 && last().pots.size == 0) removeLast() // Remove all empty breaks, except initial one
    return pot
}

fun MutableList<DomainBreak>.getDisplayShots(): MutableList<DomainBreak> {
    val list = mutableListOf<DomainBreak>() // Create a list of pots show within the break rv (SAFE, MISS, REMOVERED are not shown)
    forEach {
        if (it.pots.last().potType in listOf(TYPE_HIT, TYPE_FREE, TYPE_FOUL, TYPE_ADDRED, TYPE_REMOVERED)
            && it.pots.last().ball != NOBALL()
        ) list.add(it.copy())
    }
    return list
}