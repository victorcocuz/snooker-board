package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.domain.models.BallType.TYPE_BLACK
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_BLUE
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_BROWN
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_COLOR
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_FREEBALL
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_FREEBALLAVAILABLE
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_FREEBALLTOGGLE
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_GREEN
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_NOBALL
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_PINK
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_RED
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_WHITE
import com.quickpoint.snookerboard.domain.models.BallType.TYPE_YELLOW
import com.quickpoint.snookerboard.domain.models.DomainBall.BLACK
import com.quickpoint.snookerboard.domain.models.DomainBall.BLUE
import com.quickpoint.snookerboard.domain.models.DomainBall.BROWN
import com.quickpoint.snookerboard.domain.models.DomainBall.COLOR
import com.quickpoint.snookerboard.domain.models.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.models.DomainBall.FREEBALLAVAILABLE
import com.quickpoint.snookerboard.domain.models.DomainBall.FREEBALLTOGGLE
import com.quickpoint.snookerboard.domain.models.DomainBall.GREEN
import com.quickpoint.snookerboard.domain.models.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.models.DomainBall.PINK
import com.quickpoint.snookerboard.domain.models.DomainBall.RED
import com.quickpoint.snookerboard.domain.models.DomainBall.WHITE
import com.quickpoint.snookerboard.domain.models.DomainBall.YELLOW
import com.quickpoint.snookerboard.domain.models.PotAction.RETAKE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_ADDRED
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FOUL
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FOUL_ATTEMPT
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FREE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_FREE_ACTIVE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_HIT
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_LAST_BLACK_FOULED
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_MISS
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_REMOVE_COLOR
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_REMOVE_RED
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_RESPOT_BLACK
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_SAFE
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_SAFE_MISS
import com.quickpoint.snookerboard.domain.models.PotType.TYPE_SNOOKER
import com.quickpoint.snookerboard.domain.utils.MatchSettings

// The DOMAIN Ball is the simplest game data unit. It stores ball information
enum class BallType { TYPE_NOBALL, TYPE_WHITE, TYPE_RED, TYPE_YELLOW, TYPE_GREEN, TYPE_BROWN, TYPE_BLUE, TYPE_PINK, TYPE_BLACK, TYPE_COLOR, TYPE_FREEBALL, TYPE_FREEBALLTOGGLE, TYPE_FREEBALLAVAILABLE }

val listOfBallsColors = listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
val listOfBallsPlayable = listOf(RED(), YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK(), WHITE())

sealed class DomainBall(
    var ballId: Long = MatchSettings.uniqueId,
    var ballType: BallType,
    var points: Int,
    var foul: Int,
) {

    class NOBALL(ballId: Long = MatchSettings.uniqueId, points: Int = 0, foul: Int = 0) : DomainBall(ballId, TYPE_NOBALL, points, foul)
    class WHITE(ballId: Long = MatchSettings.uniqueId, points: Int = 0, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_WHITE, points, foul)

    class RED(ballId: Long = MatchSettings.uniqueId, points: Int = 1, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_RED, points, foul)

    class YELLOW(ballId: Long = MatchSettings.uniqueId, points: Int = 2, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_YELLOW, points, foul)

    class GREEN(ballId: Long = MatchSettings.uniqueId, points: Int = 3, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_GREEN, points, foul)

    class BROWN(ballId: Long = MatchSettings.uniqueId, points: Int = 4, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_BROWN, points, foul)

    class BLUE(ballId: Long = MatchSettings.uniqueId, points: Int = 5, foul: Int = 5 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_BLUE, points, foul)

    class PINK(ballId: Long = MatchSettings.uniqueId, points: Int = 6, foul: Int = 6 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_PINK, points, foul)

    class BLACK(ballId: Long = MatchSettings.uniqueId, points: Int = 7, foul: Int = 7 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_BLACK, points, foul)

    class COLOR(ballId: Long = MatchSettings.uniqueId, points: Int = 1, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_COLOR, points, foul)

    class FREEBALL(ballId: Long = 0, points: Int = 1, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_FREEBALL, points, foul)

    class FREEBALLAVAILABLE(ballId: Long = 0, points: Int = 1, foul: Int = 4 + MatchSettings.foulModifier) :
        DomainBall(ballId, TYPE_FREEBALLAVAILABLE, points, foul)

    class FREEBALLTOGGLE(ballId: Long = 0, points: Int = 0, foul: Int = 0) : DomainBall(ballId, TYPE_FREEBALLTOGGLE, points, foul)

    fun getBallOrdinal(): Int { // Get a numeric correspondent for each ball to store in database
        return when (ballType) {
            TYPE_NOBALL -> 0
            TYPE_WHITE -> 1
            TYPE_RED -> 2
            TYPE_YELLOW -> 3
            TYPE_GREEN -> 4
            TYPE_BROWN -> 5
            TYPE_BLUE -> 6
            TYPE_PINK -> 7
            TYPE_BLACK -> 8
            TYPE_COLOR -> 9
            TYPE_FREEBALL -> 10
            TYPE_FREEBALLAVAILABLE -> 11
            TYPE_FREEBALLTOGGLE -> 12
        }
    }
}

// Checker methods
fun List<DomainBall>.isLastBall() = size == 1
fun List<DomainBall>.isLastBlack() = size == 2
fun List<DomainBall>.isInColors() = if (MatchSettings.isFreeballEnabled) size <= 8 else size <= 7
fun List<DomainBall>.isInColorsWithFreeBall() = size <= 8
fun List<DomainBall>.wasPreviousBallColor() = size in (7..37).filter { it % 2 == 1 }
fun List<DomainBall>.isThisBallColorAndNotLast() = size in (10..38).filter { it % 2 == 0 }
fun List<DomainBall>.isAddRedAvailable() = isThisBallColorAndNotLast() && !MatchSettings.isFreeballEnabled
fun List<DomainBall>.redsRemaining() = (size - 7) / 2

fun List<DomainBall>.redsOnTheTable(): Int {
    var counter = 0
    forEach {
        if (it.ballType == TYPE_RED) counter++
    }
    return counter
}

fun List<DomainBall>.maxRemoveReds() = minOf(redsOnTheTable(), 3)

// Helper methods
fun MutableList<DomainBall>.foulValue() = if (size > 4) 4 else (7 - 2 * (size - 1))
fun List<DomainBall>?.availablePoints(): Int {
    if (this == null) return 0
    val freeSize = (if (MatchSettings.isFreeballEnabled) size - 1 else size)
    return if (freeSize <= 7) (-(8 - freeSize) * ((8 - freeSize) + 1) + 56) / 2 + (if (MatchSettings.isFreeballEnabled) (9 - freeSize) else 0)
    else 27 + ((size - 7) / 2) * 8 + (if (size % 2 == 0) 7 else 0)
}

// Frame methods
fun MutableList<DomainBall>.resetBalls() {
    clear()
    addNextBalls(MatchSettings.availableReds * 2 + 7)
}

fun MutableList<DomainBall>.onPot(potType: PotType, potAction: PotAction) {
    when (potType) {
        TYPE_HIT, TYPE_REMOVE_COLOR, TYPE_FREE -> removeBalls(1)
        TYPE_ADDRED, TYPE_REMOVE_RED -> removeBalls(2)
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> {
            if (last() is COLOR && potAction != RETAKE) removeBalls(1)
            if (last() is FREEBALL) {
                removeFreeBall()
            }
        }
        TYPE_FREE_ACTIVE -> if (MatchSettings.isFreeballEnabled) addFreeBall(1) else removeFreeBall()
        TYPE_LAST_BLACK_FOULED -> removeBalls(1)
        TYPE_RESPOT_BLACK -> addBalls(BLACK())
        TYPE_FOUL_ATTEMPT -> {}
    }
}

fun MutableList<DomainBall>.onUndo(potType: PotType, potAction: PotAction, frameStack: MutableList<DomainBreak>) {
    when (potType) {
        TYPE_HIT, TYPE_REMOVE_COLOR -> addNextBalls(1)
        TYPE_FREE -> addFreeBall(0)
        TYPE_ADDRED, TYPE_REMOVE_RED -> addNextBalls(2)
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> {
            when (frameStack.lastPotType()) {
                TYPE_REMOVE_RED -> if (frameStack.lastBallTypeBeforeRemoveBall() == TYPE_RED) addNextBalls(1)
                TYPE_HIT -> if (frameStack.lastBallType() == TYPE_RED && potAction != RETAKE) addNextBalls(1)
                TYPE_FREE -> if (!isInColors()) addNextBalls(1) // Adds a color to the ballstack
                TYPE_FREE_ACTIVE -> addFreeBall(1)
                else -> {}
            }
        }
        TYPE_FREE_ACTIVE -> if (MatchSettings.isFreeballEnabled) removeFreeBall() else addFreeBall(1)
        TYPE_LAST_BLACK_FOULED -> addNextBalls(1)
        TYPE_RESPOT_BLACK -> removeBalls(1)
        TYPE_FOUL_ATTEMPT -> {}
    }
}

// Private methods for adding balls
internal fun MutableList<DomainBall>.addNextBalls(number: Int) = repeat(number) {
    if (size >= 37) return
    add(when (size) {
        0 -> WHITE()
        1 -> BLACK()
        2 -> PINK()
        3 -> BLUE()
        4 -> BROWN()
        5 -> GREEN()
        6 -> YELLOW()
        in (7..37).filter { it % 2 == 0 } -> RED()
        in (7..37).filter { it % 2 == 1 } -> COLOR()
        else -> NOBALL() // Will add a NOBALL() when size is 0
    }
    )
    last().ballId = MatchSettings.uniqueId
}

internal fun MutableList<DomainBall>.addBalls(vararg balls: DomainBall): Int {
    val points = when {
        isInColors() -> last().points
        !wasPreviousBallColor() -> 0
        else -> 8
    }
    for (ball in balls) {
        ball.ballId = MatchSettings.uniqueId
        add(ball)
    }
    return points
}

internal fun MutableList<DomainBall>.addFreeBall(pol: Int) {
    MatchSettings.maxFramePoints += if (isInColors() || !wasPreviousBallColor()) {
        addBalls(FREEBALL(points = last().points)) * pol
    } else {
        addBalls(COLOR(), FREEBALL()) * pol
    }
}

// Private methods for removing balls
internal fun MutableList<DomainBall>.removeBalls(times: Int): Int = if (times == 1) {
    removeLast().points * (-1)
} else {
    repeat(times) { removeLast() }
    8 * (-1)
}

fun MutableList<DomainBall>.removeFreeBall() {
    MatchSettings.maxFramePoints += removeBalls(if (isInColorsWithFreeBall()) 1 else 2)
}

// Converter methods
fun BallType?.getBallFromValues(ballPoints: Int?): DomainBall {
    val ball = when (this) {
        TYPE_NOBALL -> NOBALL()
        TYPE_WHITE -> WHITE()
        TYPE_RED -> RED()
        TYPE_YELLOW -> YELLOW()
        TYPE_GREEN -> GREEN()
        TYPE_BROWN -> BROWN()
        TYPE_BLUE -> BLUE()
        TYPE_PINK -> PINK()
        TYPE_BLACK -> BLACK()
        TYPE_COLOR -> COLOR()
        TYPE_FREEBALL -> FREEBALL()
        TYPE_FREEBALLAVAILABLE -> FREEBALLAVAILABLE()
        TYPE_FREEBALLTOGGLE -> FREEBALLTOGGLE()
        null -> NOBALL()
    }
    ballPoints?.let { ball.points = it }
    return ball
}

fun removeBallsForFoulDialog(ballList: List<DomainBall>): MutableList<DomainBall> {
    val foulStack = mutableListOf<DomainBall>()
    ballList.forEach { foulStack.add(it) }
    foulStack.apply {
        forEachIndexed { index, domainBall ->
            if (domainBall.ballType in listOf(TYPE_FREEBALL, TYPE_NOBALL, TYPE_COLOR)) removeAt(index)
        }
        return asReversed()
    }
}

fun List<DomainBall>.bindMatchBalls() = when (lastOrNull()) {
    is COLOR -> listOfBallsColors
    is WHITE -> listOf(NOBALL())
    null -> emptyList()
    else -> listOf(last())
}

fun List<DomainBall>.bindFoulBalls() = when (size) {
    0 -> emptyList()
    in (2..8) -> removeBallsForFoulDialog(this)
    else -> listOfBallsPlayable
}