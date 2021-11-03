package com.quickpoint.snookerboard.domain

// The DOMAIN Ball is the simplest game data unit. It stores ball information
sealed class DomainBall(
    var points: Int,
    var foul: Int
) {
    class NOBALL(points: Int = 0, foul: Int = 0) : DomainBall(points , foul)
    class WHITE(points: Int = 0, foul: Int = 4) : DomainBall(points, foul)
    class RED(points: Int = 1, foul: Int = 4) : DomainBall(points, foul)
    class YELLOW(points: Int = 2, foul: Int = 4) : DomainBall(points, foul)
    class GREEN(points: Int = 3, foul: Int = 4) : DomainBall(points, foul)
    class BROWN(points: Int = 4, foul: Int = 4) : DomainBall(points, foul)
    class BLUE(points: Int = 5, foul: Int = 5) : DomainBall(points, foul)
    class PINK(points: Int = 6, foul: Int = 6) : DomainBall(points, foul)
    class BLACK(points: Int = 7, foul: Int = 7) : DomainBall(points, foul)
    class COLOR(points: Int = 1, foul: Int = 4) : DomainBall(points, foul)
    class FREEBALL(points: Int = 1, foul: Int = 4) : DomainBall(points, foul)

    fun setCustomPointValue(points: Int) { // Point values can be overwritten (e.g. assigning a freeball value)
        this.points = points
    }

    fun setCustomFoulValue(foul: Int) { // Foul values can be overwritten (e.g. potting white when min foul > 4)
        this.foul = foul
    }

    fun getBallOrdinal() : Int { // Get a numeric correspondent for each ball to store in database
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

// CONVERTER method from values to DOMAIN Ball
fun getBallFromValues(position: Int, points: Int, foul: Int) : DomainBall { // Return a DOMAIN ball from a list of values
    return when(position) {
        0 -> DomainBall.NOBALL(points, foul)
        1 -> DomainBall.WHITE(points, foul)
        2 -> DomainBall.RED(points, foul)
        3 -> DomainBall.YELLOW(points, foul)
        4 -> DomainBall.GREEN(points, foul)
        5 -> DomainBall.BROWN(points, foul)
        6 -> DomainBall.BLUE(points, foul)
        7 -> DomainBall.PINK(points, foul)
        8 -> DomainBall.BLACK(points, foul)
        9 -> DomainBall.COLOR(points, foul)
        else -> DomainBall.FREEBALL()
    }
}

// Helpers
fun MutableList<DomainBall>.inColors(): Boolean = this.size <= 7
fun MutableList<DomainBall>.isNextColor(): Boolean = this.size in (7..37).filter { it % 2 != 0 }
fun MutableList<DomainBall>.removeBalls(times: Int): Int = if (times == 1) {
    this.removeLast().points
} else {
    repeat(times) { this.removeLast() }
    8
}
fun MutableList<DomainBall>.addBalls(vararg balls: DomainBall) {
    for (ball in balls) this.add(ball)
}
fun MutableList<DomainBall>.addFreeBall(): Int {
    return if (inColors()) {
        addBalls(DomainBall.FREEBALL())
        last().points
    } else {
        addBalls(DomainBall.COLOR(), DomainBall.FREEBALL())
        8
    }
}
fun MutableList<DomainBall>.removeFreeBall(): Int = if (inColors()) removeBalls(1) else removeBalls(2)