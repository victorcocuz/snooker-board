package com.quickpoint.snookerboard.domain

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

fun getBallFromValues(position: Int, points: Int, foul: Int) : DomainBall {
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