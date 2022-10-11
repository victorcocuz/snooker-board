package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.BallType.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.*
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.domain.PotType.*
import timber.log.Timber

// The DOMAIN Ball is the simplest game data unit. It stores ball information
enum class BallType { TYPE_NOBALL, TYPE_WHITE, TYPE_RED, TYPE_YELLOW, TYPE_GREEN, TYPE_BROWN, TYPE_BLUE, TYPE_PINK, TYPE_BLACK, TYPE_COLOR, TYPE_FREEBALL, TYPE_FREEBALLTOGGLE, TYPE_FREEBALLAVAILABLE }
sealed class DomainBall(
    var ballType: BallType,
    var points: Int,
    var foul: Int,
) {
    class NOBALL(points: Int = 0, foul: Int = 0) : DomainBall(TYPE_NOBALL, points, foul)
    class WHITE(points: Int = 0, foul: Int = 4) : DomainBall(TYPE_WHITE, points, foul)
    class RED(points: Int = 1, foul: Int = 4) : DomainBall(TYPE_RED, points, foul)
    class YELLOW(points: Int = 2, foul: Int = 4) : DomainBall(TYPE_YELLOW, points, foul)
    class GREEN(points: Int = 3, foul: Int = 4) : DomainBall(TYPE_GREEN, points, foul)
    class BROWN(points: Int = 4, foul: Int = 4) : DomainBall(TYPE_BROWN, points, foul)
    class BLUE(points: Int = 5, foul: Int = 5) : DomainBall(TYPE_BLUE, points, foul)
    class PINK(points: Int = 6, foul: Int = 6) : DomainBall(TYPE_PINK, points, foul)
    class BLACK(points: Int = 7, foul: Int = 7) : DomainBall(TYPE_BLACK, points, foul)
    class COLOR(points: Int = 1, foul: Int = 4) : DomainBall(TYPE_COLOR, points, foul)
    class FREEBALL(points: Int = 1, foul: Int = 4) : DomainBall(TYPE_FREEBALL, points, foul)
    class FREEBALLAVAILABLE(points: Int = 1, foul: Int = 4) : DomainBall(TYPE_FREEBALLAVAILABLE, points, foul)
    class FREEBALLTOGGLE(points: Int = 0, foul: Int = 0) : DomainBall(TYPE_FREEBALLTOGGLE, points, foul)

    fun setCustomPointValue(points: Int) { // Point values can be overwritten (e.g. assigning a freeball value)
        this.points = points
    }

    fun setCustomFoulValue(foul: Int) { // Foul values can be overwritten (e.g. potting white when min foul > 4)
        this.foul = foul
    }

    fun getBallOrdinal(): Int { // Get a numeric correspondent for each ball to store in database
        return when (this) {
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
            is FREEBALLAVAILABLE -> 11
            is FREEBALLTOGGLE -> 12
        }
    }
}

// CONVERTER method from values to DOMAIN Ball
fun getBallFromValues(position: Int, points: Int, foul: Int): DomainBall { // Return a DOMAIN ball from a list of values
    return when (position) {
        0 -> NOBALL(points, foul)
        1 -> WHITE(points, foul)
        2 -> RED(points, foul)
        3 -> YELLOW(points, foul)
        4 -> GREEN(points, foul)
        5 -> BROWN(points, foul)
        6 -> BLUE(points, foul)
        7 -> PINK(points, foul)
        8 -> BLACK(points, foul)
        9 -> COLOR(points, foul)
        10 -> FREEBALL(points, foul)
        11 -> FREEBALLAVAILABLE()
        else -> FREEBALLTOGGLE()
    }
}

// Helpers
fun MutableList<DomainBall>.isInColors(): Boolean = this.size <= 7
fun MutableList<DomainBall>.isNextColor(): Boolean = this.size in (7..37).filter { it % 2 != 0 }
fun MutableList<DomainBall>.removeBalls(times: Int): Int = if (times == 1) {
    this.removeLast().points * (-1)
} else {
    repeat(times) { this.removeLast() }
    8 * (-1)
}

fun MutableList<DomainBall>.addBalls(vararg balls: DomainBall) {
    for (ball in balls) this.add(ball)
}

fun MutableList<DomainBall>.addFreeBall(): Int {
    return if (isInColors()) {
        addBalls(FREEBALL())
        last().points
    } else {
        addBalls(COLOR(), FREEBALL())
        8
    }
}

fun MutableList<DomainBall>.removeFreeBall(): Int = if (isInColors()) removeBalls(1) else removeBalls(2)

fun MutableList<DomainBall>.rerackBalls() {
    clear()
    addBalls(WHITE(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
    repeat(RULES.reds) { addBalls(COLOR(), RED()) }
}

fun MutableList<DomainBall>.handlePotBallStack(potType: PotType, isFrameEqual: Boolean) {
    when (potType) {
        TYPE_HIT, TYPE_FREE -> removeBalls(1)
        TYPE_FREEAVAILABLE -> {}
        TYPE_FREETOGGLE -> RULES.frameMax += if (FREEBALLINFO.isSelected) addFreeBall() else removeFreeBall()
        TYPE_REMOVERED, TYPE_ADDRED -> removeBalls(2)
        TYPE_SAFE, TYPE_MISS, TYPE_FOUL -> {
            if (last() is COLOR) removeBalls(1)
            if (last() is FREEBALL) RULES.frameMax += removeFreeBall()
        }
    }
    if (size == 1) if (isFrameEqual) addBalls(BLACK()) // Query end frame exception; see fragment
}

fun MutableList<DomainBall>.handleUndoBallStack(pot: DomainPot, lastBallType: BallType) {
    when (pot.potType) {
        TYPE_HIT -> addBalls(if (isNextColor()) COLOR() else pot.ball)
        TYPE_ADDRED -> addBalls(RED(), COLOR())
        TYPE_FREE -> addBalls(FREEBALL())
        TYPE_REMOVERED -> if (isNextColor()) addBalls(COLOR(), RED()) else addBalls(RED(), COLOR())
        TYPE_SAFE, TYPE_MISS, TYPE_FOUL -> when (lastBallType) {
            TYPE_RED, TYPE_FREEBALL -> if (isNextColor()) addBalls(COLOR())
            TYPE_FREEBALLTOGGLE -> RULES.frameMax += addFreeBall()
            else -> {}
        }

        TYPE_FREEAVAILABLE -> {}
        TYPE_FREETOGGLE -> RULES.frameMax += if (FREEBALLINFO.isSelected) removeFreeBall() else addFreeBall()
    }
}
