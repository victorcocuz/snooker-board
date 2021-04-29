package com.example.snookerscore.domain

import com.example.snookerscore.database.DatabaseBall
import com.example.snookerscore.database.DatabaseBreak
import com.example.snookerscore.database.DatabasePot

sealed class Ball(
    var points: Int,
    var foul: Int
) {
    class NOBALL(points: Int = 0, foul: Int = 0) : Ball(points , foul)
    class WHITE(points: Int = 0, foul: Int = 4) : Ball(points, foul)
    class RED(points: Int = 1, foul: Int = 4) : Ball(points, foul)
    class YELLOW(points: Int = 2, foul: Int = 4) : Ball(points, foul)
    class GREEN(points: Int = 3, foul: Int = 4) : Ball(points, foul)
    class BROWN(points: Int = 4, foul: Int = 4) : Ball(points, foul)
    class BLUE(points: Int = 5, foul: Int = 5) : Ball(points, foul)
    class PINK(points: Int = 6, foul: Int = 6) : Ball(points, foul)
    class BLACK(points: Int = 7, foul: Int = 7) : Ball(points, foul)
    class COLOR(points: Int = 1, foul: Int = 4) : Ball(points, foul)
    class FREEBALL(points: Int = 1, foul: Int = 4) : Ball(points, foul)

    fun assignNewPoints(points: Int) {
        this.points = points
    }

    fun assignNewFoul(foul: Int) {
        this.foul = foul
    }

    fun getBallOrdinal() : Int {
        return when(this) {
            is NOBALL -> 0
            is WHITE -> 1
            is RED -> 2
            is YELLOW -> 3
            is GREEN -> 4
            is BROWN -> 5
            is BLUE -> 6
            is PINK -> 7
            is BLACK -> 8
            is COLOR -> 9
            is FREEBALL -> 10
        }
    }
}

fun getBallFromValues(position: Int, points: Int, foul: Int) : Ball {
    return when(position) {
        0 -> Ball.NOBALL(points, foul)
        1 -> Ball.WHITE(points, foul)
        2 -> Ball.RED(points, foul)
        3 -> Ball.YELLOW(points, foul)
        4 -> Ball.GREEN(points, foul)
        5 -> Ball.BROWN(points, foul)
        6 -> Ball.BLUE(points, foul)
        7 -> Ball.PINK(points, foul)
        8 -> Ball.BLACK(points, foul)
        9 -> Ball.COLOR(points, foul)
        else -> Ball.FREEBALL()
    }
}


enum class PotType { HIT, FREE, SAFE, MISS, FOUL, REMOVERED, ADDRED }
enum class PotAction { CONTINUE, SWITCH }

sealed class Pot(
    val ball: Ball,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: Ball) : Pot(ball, PotType.HIT, PotAction.CONTINUE)
    object SAFE : Pot(Ball.NOBALL(), PotType.SAFE, PotAction.SWITCH)
    object MISS : Pot(Ball.NOBALL(), PotType.MISS, PotAction.SWITCH)
    object FREEMISS : Pot(Ball.NOBALL(), PotType.FREE, PotAction.CONTINUE)
    class FOUL(ball: Ball, action: PotAction) : Pot(ball, PotType.FOUL, action)
    object REMOVERED : Pot(Ball.NOBALL(), PotType.REMOVERED, PotAction.CONTINUE)
    object ADDRED : Pot(Ball.RED(), PotType.ADDRED, PotAction.CONTINUE)
}

data class Break(
    val breakId: Int,
    val player: Int,
    val frameCount: Int,
    val pots: MutableList<Pot>,
    var breakSize: Int
)

fun List<Break>.asDatabaseBreak(): List<DatabaseBreak> {
    return map {
        DatabaseBreak(
            player = it.player,
            frameCount = it.frameCount,
            breakId = it.breakId,
            breakSize = it.breakSize
        )
    }
}

fun Break.asDatabasePot(): List<DatabasePot> {
    return pots.map { pot ->
        DatabasePot(
            breakId = breakId,
            ball = pot.ball.getBallOrdinal(),
            ballPoints = pot.ball.points,
            ballFoul = pot.ball.foul,
            potType = pot.potType.ordinal,
            potAction = pot.potAction.ordinal
        )
    }
}

fun List<Ball>.asDatabaseBallStack(): List<DatabaseBall> {
    return this.map { ball ->
        DatabaseBall(
            ballValue = ball.getBallOrdinal(),
            ballPoints = ball.points,
            ballFoul = ball.foul
        )
    }
}
