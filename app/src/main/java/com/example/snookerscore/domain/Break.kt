package com.example.snookerscore.domain

import com.example.snookerscore.database.DatabaseMatchBall
import com.example.snookerscore.database.DatabaseMatchBreak
import com.example.snookerscore.database.DatabaseMatchPot

enum class Ball(
    val points: Int,
    val foul: Int
) {
    NOBALL(0, 0),
    WHITE(4, 4),
    RED(1, 4),
    YELLOW(2, 4),
    GREEN(3, 4),
    BROWN(4, 4),
    BLUE(5, 5),
    PINK(6, 6),
    BLACK(7, 7),
    COLOR(1, 4),
    FREEBALL(1, 4)
}

enum class PotType { HIT, FREE, SAFE, MISS, FOUL, REMOVERED, ADDRED }
enum class PotAction { CONTINUE, SWITCH }

sealed class Pot(
    val ball: Ball,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: Ball) : Pot(ball, PotType.HIT, PotAction.CONTINUE)
    object SAFE : Pot(Ball.NOBALL, PotType.SAFE, PotAction.SWITCH)
    object MISS : Pot(Ball.NOBALL, PotType.MISS, PotAction.SWITCH)
    object FREEMISS: Pot(Ball.NOBALL, PotType.FREE, PotAction.CONTINUE)
    class FOUL(ball: Ball, action: PotAction): Pot(ball, PotType.FOUL, action)
    object REMOVERED: Pot(Ball.NOBALL, PotType.REMOVERED, PotAction.CONTINUE)
    object ADDRED: Pot(Ball.RED, PotType.ADDRED, PotAction.CONTINUE)
}

data class Break(
    val breakId: Int,
    val player: Int,
    val frameCount: Int,
    val pots: MutableList<Pot>,
    var breakSize: Int
)

fun List<Break>.asDatabaseBreak(): List<DatabaseMatchBreak> {
    return map {
        DatabaseMatchBreak(
            player = it.player,
            frameCount = it.frameCount,
            breakId = it.breakId,
            breakSize = it.breakSize
        )
    }
}

fun Break.asDatabasePot(): List<DatabaseMatchPot> {
    return pots.map { pot ->
        DatabaseMatchPot(
            breakId = breakId,
            ball = pot.ball.ordinal,
            potType = pot.potType.ordinal,
            potAction = pot.potAction.ordinal
        )
    }
}

fun List<Ball>.asDatabaseBallStack(): List<DatabaseMatchBall> {
    return this.map { ball ->
        DatabaseMatchBall(
            ballValue = ball.ordinal
        )
    }
}
