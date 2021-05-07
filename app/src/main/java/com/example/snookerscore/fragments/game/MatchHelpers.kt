package com.example.snookerscore.fragments.game

import com.example.snookerscore.domain.CurrentScore
import com.example.snookerscore.domain.DomainBall
import com.example.snookerscore.domain.DomainBall.*
import com.example.snookerscore.domain.DomainBreak
import com.example.snookerscore.domain.DomainPot
import com.example.snookerscore.domain.PotType.*
import kotlin.math.max

// FrameStack
fun MutableList<DomainBreak>.isPreviousRed() = this.last().pots.last().ball is RED
fun MutableList<DomainBreak>.addToFrameStack(pot: DomainPot, playerAsInt: Int, frameCount: Int) {
    if (pot.potType !in listOf(HIT, FREE, ADDRED)
        || this.size == 0
        || this.last().pots.last().potType !in listOf(HIT, FREE, ADDRED)
        || this.last().player != playerAsInt
    ) this.add(
        DomainBreak(
//            1 + (this.lastOrNull()?.breakId ?: 0),
            playerAsInt,
            frameCount,
            mutableListOf(),
            0
        )
    )
    this.last().pots.add(pot)
    if (pot.potType in listOf(HIT, FREE, ADDRED)) this.last().breakSize += pot.ball.points
}

fun MutableList<DomainBreak>.removeFromFrameStack(): DomainPot {
    val crtPot = this.last().pots.removeLast()
    if (crtPot.potType in listOf(HIT, FREE, ADDRED)) this.last().breakSize -= crtPot.ball.points
    if (this.last().pots.size == 0) this.removeLast()
    if (this.size == 1 && this.last().pots.size == 0) this.removeLast()
    return crtPot
}

fun MutableList<DomainBreak>.getDisplayShots(): MutableList<DomainBreak> {
    val list = mutableListOf<DomainBreak>()
    this.forEach {
        if (it.pots.last().potType in listOf(HIT, ADDRED, FREE, FOUL)
            && it.pots.last().ball != NOBALL()
        ) {
            list.add(it.copy())
        }
    }
    return list
}

// Score
fun CurrentScore.calculatePoints(pot: DomainPot, pol: Int, lastBall: DomainBall, matchFoul: Int, frameStack: MutableList<DomainBreak>) {
    var points = 0
    when (pot.potType) {
        in listOf(HIT, FREE, ADDRED) -> {
            points = if (pot.potType == FREE) lastBall.points else pot.ball.points
            addFramePoints(pol * points)
            pot.ball.assignNewPoints(points)
            addSuccessShots(pol)
        }
        FOUL -> {
            points = (matchFoul + if (pot.ball is WHITE) max(lastBall.foul, 4) else pot.ball.foul)
            pot.ball.assignNewFoul(points)
            getOther().addFramePoints(pol * points)
            addMissedShots(pol)
            addFouls(pol)
        }
        MISS -> addMissedShots(pol)
        else -> {
        }
    }
    findMaxBreak(frameStack)
}

// BallStack
fun MutableList<DomainBall>.inColors(): Boolean = this.size <= 7
fun MutableList<DomainBall>.isNextColor(): Boolean = this.size in (7..37).filter { it % 2 != 0 }
fun MutableList<DomainBall>.removeBall(times: Int = 1) = repeat(times) { this.removeLast() }
fun MutableList<DomainBall>.addBalls(vararg balls: DomainBall) {
    for (ball in balls) this.add(ball)
}