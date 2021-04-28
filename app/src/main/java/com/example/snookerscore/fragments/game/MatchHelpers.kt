package com.example.snookerscore.fragments.game

import com.example.snookerscore.domain.*
import kotlin.math.max

// FrameStack
fun MutableList<Break>.isPreviousRed() = this.last().pots.last().ball == Ball.RED
fun MutableList<Break>.addToFrameStack(pot: Pot, playerAsInt: Int, frameCount: Int) {
    if (pot.potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
        || this.size == 0
        || this.last().pots.last().potType !in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)
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
    if (pot.potType in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)) this.last().breakSize += pot.ball.points
}

fun MutableList<Break>.removeFromFrameStack(): Pot {
    val crtPot = this.last().pots.removeLast()
    if (crtPot.potType in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED)) this.last().breakSize -= crtPot.ball.points
    if (this.last().pots.size == 0) this.removeLast()
    if (this.size == 1 && this.last().pots.size == 0) this.removeLast()
    return crtPot
}

// Score
fun CurrentScore.calculatePoints(ball: Ball, potType: PotType, pol: Int, lastBall: Ball, matchFoul: Int) = this.apply {
    when (potType) {
        in listOf(PotType.HIT, PotType.FREE, PotType.ADDRED) -> {
            addFramePoints(
                pol * if (ball == Ball.FREEBALL) {
                    lastBall.points
                } else ball.points
            )
            addSuccessShots(pol)
        }
        PotType.FOUL -> {
            getOther().addFramePoints(pol * (matchFoul + if (ball == Ball.WHITE) max(lastBall.foul, 4) else ball.foul))
            addMissedShots(pol)
            addFouls(pol)
        }
        PotType.MISS -> addMissedShots(pol)
        else -> {
        }
    }
}

// BallStack
fun MutableList<Ball>.inColors(): Boolean = this.size <= 7
fun MutableList<Ball>.isNextColor(): Boolean = this.size in (7..37).filter { it % 2 != 0 }
fun MutableList<Ball>.removeBalls(times: Int = 1) = repeat(times) { this.removeLast() }
fun MutableList<Ball>.addBalls(vararg balls: Ball) {
    for (ball in balls) this.add(ball)
}