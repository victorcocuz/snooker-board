package com.quickpoint.snookerboard.domain

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.annotations.VisibleForTesting
import com.quickpoint.snookerboard.domain.BallType.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS

// The DOMAIN Ball is the simplest game data unit. It stores ball information
enum class BallType { TYPE_NOBALL, TYPE_WHITE, TYPE_RED, TYPE_YELLOW, TYPE_GREEN, TYPE_BROWN, TYPE_BLUE, TYPE_PINK, TYPE_BLACK, TYPE_COLOR, TYPE_FREEBALL, TYPE_FREEBALLTOGGLE, TYPE_FREEBALLAVAILABLE }
val listOfBallsColors = listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
val listOfBallsPlayable = listOf(RED(), YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK(), WHITE())

sealed class DomainBall(
    var ballId: Long,
    var ballType: BallType,
    var points: Int,
    var foul: Int,
) {

    class NOBALL(ballId: Long = 0, points: Int = 0, foul: Int = 0) : DomainBall(ballId, TYPE_NOBALL, points, foul)
    class WHITE(ballId: Long = 0, points: Int = 0, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_WHITE, points, foul)
    class RED(ballId: Long = 0, points: Int = 1, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_RED, points, foul)
    class YELLOW(ballId: Long = 0, points: Int = 2, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_YELLOW, points, foul)
    class GREEN(ballId: Long = 0, points: Int = 3, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_GREEN, points, foul)
    class BROWN(ballId: Long = 0, points: Int = 4, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_BROWN, points, foul)
    class BLUE(ballId: Long = 0, points: Int = 5, foul: Int = 5 + SETTINGS.foul) : DomainBall(ballId, TYPE_BLUE, points, foul)
    class PINK(ballId: Long = 0, points: Int = 6, foul: Int = 6 + SETTINGS.foul) : DomainBall(ballId, TYPE_PINK, points, foul)
    class BLACK(ballId: Long = 0, points: Int = 7, foul: Int = 7 + SETTINGS.foul) : DomainBall(ballId, TYPE_BLACK, points, foul)
    class COLOR(ballId: Long = 0, points: Int = 1, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_COLOR, points, foul)
    class FREEBALL(ballId: Long = 0, points: Int = 1, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_FREEBALL, points, foul)
    class FREEBALLAVAILABLE(ballId: Long = 0, points: Int = 1, foul: Int = 4 + SETTINGS.foul) : DomainBall(ballId, TYPE_FREEBALLAVAILABLE, points, foul)
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
fun MutableList<DomainBall>.isLastBall() = size == 1
fun MutableList<DomainBall>.isInColors() = size <= 7
fun MutableList<DomainBall>.isInColorsWithFreeBall() = size <= 8
fun MutableList<DomainBall>.areRedsOnTheTable() = size >= 9
fun MutableList<DomainBall>.wasPreviousBallColor() = size in (7..37).filter { it % 2 == 1 }
fun MutableList<DomainBall>.isThisBallColorAndNotLast() = size in (10..38).filter { it % 2 == 0 }
fun MutableList<DomainBall>.isAddRedAvailable() = isThisBallColorAndNotLast() && !FREEBALLINFO.isVisible

// Helper methods
fun MutableList<DomainBall>.foulValue() = if (size > 4) 4 else (7 - 2 * (size - 1))
fun MutableList<DomainBall>?.availablePoints(): Int { // Formula to calculate remaining max available points
    if (this == null) return 0
    val freeSize = (if (FREEBALLINFO.isSelected) size - 1 else size)
    return if (freeSize <= 7) (-(8 - freeSize) * ((8 - freeSize) + 1) + 56) / 2 + (if (FREEBALLINFO.isSelected) (9 - freeSize) else 0)
    else 27 + ((size - 7) / 2) * 8 + (if (size % 2 == 0) 7 else 0)
}

// Frame methods
fun MutableList<DomainBall>.resetBalls() {
    clear()
    addNextBalls(SETTINGS.reds * 2 + 7)
}

fun MutableList<DomainBall>.handlePotBallStack(potType: PotType) {
    when (potType) {
        TYPE_HIT, TYPE_REMOVE_COLOR, TYPE_FREE -> removeBalls(1)
        TYPE_ADDRED, TYPE_REMOVE_RED -> removeBalls(2)
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> {
            if (last() is COLOR) removeBalls(1)
            if (last() is FREEBALL) removeFreeBall()
        }
        TYPE_FREE_TOGGLE -> if (FREEBALLINFO.isSelected) addFreeBall(1) else removeFreeBall()
        TYPE_RESPOT_BLACK -> addBalls(BLACK())
        TYPE_FREE_AVAILABLE, TYPE_FOUL_ATTEMPT -> {}
    }
}

fun MutableList<DomainBall>.handleUndoBallStack(potType: PotType, lastBall: DomainBall?) {
    when (potType) {
        TYPE_HIT, TYPE_REMOVE_COLOR -> addNextBalls(1)
        TYPE_FREE -> addFreeBall(0)
        TYPE_ADDRED, TYPE_REMOVE_RED -> addNextBalls(2)
        TYPE_SAFE, TYPE_MISS, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL -> {
            when (lastBall?.ballType) {
                TYPE_RED -> addNextBalls(1)
                TYPE_FREEBALL -> if (!isInColors()) addNextBalls(1) // Adds a color to the ballstack
                TYPE_FREEBALLTOGGLE -> addFreeBall(1)
                else -> {}
            }
        }
        TYPE_FREE_TOGGLE -> if (FREEBALLINFO.isSelected) removeFreeBall() else addFreeBall(1)
        TYPE_RESPOT_BLACK -> removeBalls(1)
        TYPE_FREE_AVAILABLE, TYPE_FOUL_ATTEMPT -> {}
    }
}

// Private methods for adding balls
@VisibleForTesting
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
    last().ballId = SETTINGS.assignUniqueId()
}

@VisibleForTesting
internal fun MutableList<DomainBall>.addBalls(vararg balls: DomainBall): Int {
    val points = when {
        isInColors() -> last().points
        !wasPreviousBallColor() -> 0
        else -> 8
    }
    for (ball in balls) {
        ball.ballId = SETTINGS.assignUniqueId()
        add(ball)
    }
    return points
}

@VisibleForTesting
internal fun MutableList<DomainBall>.addFreeBall(pol: Int) {
    SETTINGS.maxAvailable += if (isInColors() || !wasPreviousBallColor()) {
        addBalls(FREEBALL(points = last().points)) * pol
    } else {
        addBalls(COLOR(), FREEBALL()) * pol
    }
}

// Private methods for removing balls
@VisibleForTesting
internal fun MutableList<DomainBall>.removeBalls(times: Int): Int = if (times == 1) {
    removeLast().points * (-1)
} else {
    repeat(times) { removeLast() }
    8 * (-1)
}

fun MutableList<DomainBall>.removeFreeBall() {
    SETTINGS.maxAvailable += removeBalls(if (isInColorsWithFreeBall()) 1 else 2)
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

fun removeBallsForFoulDialog(ballList: MutableList<DomainBall>): MutableList<DomainBall> {
    val foulStack = mutableListOf<DomainBall>()
    ballList.forEach{foulStack.add(it)}
    foulStack.apply {
        forEachIndexed { index, domainBall ->
            if (domainBall.ballType in listOf(TYPE_FREEBALL, TYPE_NOBALL, TYPE_COLOR)) removeAt(index)
        }
        return asReversed()
    }
}