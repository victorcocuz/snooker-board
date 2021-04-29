package com.example.snookerscore.fragments.game

import com.example.snookerscore.domain.Ball
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.Break
import com.example.snookerscore.domain.CurrentScore
import com.example.snookerscore.domain.Pot
import com.example.snookerscore.domain.PotType.*
import kotlin.math.max

// FrameStack
fun MutableList<Break>.isPreviousRed() = this.last().pots.last().ball is RED
fun MutableList<Break>.addToFrameStack(pot: Pot, playerAsInt: Int, frameCount: Int) {
    if (pot.potType !in listOf(HIT, FREE, ADDRED)
        || this.size == 0
        || this.last().pots.last().potType !in listOf(HIT, FREE, ADDRED)
        || this.last().player != playerAsInt
    ) this.add(
        Break(
            1 + (this.lastOrNull()?.breakId ?: 0),
            playerAsInt,
            frameCount,
            mutableListOf(),
            0
        )
    )
    this.last().pots.add(pot)
    if (pot.potType in listOf(HIT, FREE, ADDRED)) this.last().breakSize += pot.ball.points
}

fun MutableList<Break>.removeFromFrameStack(): Pot {
    val crtPot = this.last().pots.removeLast()
    if (crtPot.potType in listOf(HIT, FREE, ADDRED)) this.last().breakSize -= crtPot.ball.points
    if (this.last().pots.size == 0) this.removeLast()
    if (this.size == 1 && this.last().pots.size == 0) this.removeLast()
    return crtPot
}

fun MutableList<Break>.getDisplayShots(): MutableList<Break> {
    val list = mutableListOf<Break>()
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
fun CurrentScore.calculatePoints(pot: Pot, pol: Int, lastBall: Ball, matchFoul: Int): Pot {
    var points = 0
    this.apply {
        when (pot.potType) {
            in listOf(HIT, FREE, ADDRED) -> {
                points = if (pot.potType == FREE) lastBall.points else pot.ball.points
                addFramePoints(pol * points)
                pot.ball.assignNewPoints(points)
                addSuccessShots(pol)
                return Pot.HIT(pot.ball)
            }
            FOUL -> {
                points = (matchFoul + if (pot.ball is WHITE) max(lastBall.foul, 4) else pot.ball.foul)
                pot.ball.assignNewFoul(points)
                getOther().addFramePoints(pol * points)
                addMissedShots(pol)
                addFouls(pol)
                return Pot.FOUL(pot.ball, pot.potAction)
            }
            MISS -> addMissedShots(pol)
            else -> {}
        }
    }
    return pot
}

// BallStack
fun MutableList<Ball>.inColors(): Boolean = this.size <= 7
fun MutableList<Ball>.isNextColor(): Boolean = this.size in (7..37).filter { it % 2 != 0 }
fun MutableList<Ball>.removeBall(times: Int = 1) = repeat(times) { this.removeLast() }
fun MutableList<Ball>.addBalls(vararg balls: Ball) {
    for (ball in balls) this.add(ball)
}