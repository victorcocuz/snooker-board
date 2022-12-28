package com.quickpoint.snookerboard.domain

import com.google.common.truth.Truth.assertThat
import com.quickpoint.snookerboard.domain.BallType.*
import com.quickpoint.snookerboard.utils.FrameToggles
import org.junit.Test

internal class DomainBallTest {

    private fun MutableList<DomainBall>.ballTypeList(): List<BallType> = this.map { it.ballType }

    @Test
    fun handlePotBallStack_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()
        ballStack.resetBalls()

//        val listOfActions = listOf(TYPE_HIT, TYPE_ADDRED, TYPE_HIT)
//        for (action in listOfActions) ballStack.handlePotBallStack(potAction = action, potType = )
    }

    @Test
    fun availablePoints_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()

        ballStack.addNextBalls(1) // Size 1 - NOBALL()
        assertThat(ballStack.availablePoints()).isEqualTo(0)

        ballStack.addNextBalls(2) // Size 3 - PINK()
        assertThat(ballStack.availablePoints()).isEqualTo(13)

        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL, TYPE_BLACK, TYPE_PINK))
        ballStack.addFreeBall(1) // Size 4 - FREE()
        FrameToggles.FRAMETOGGLES.isFreeball = true
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL, TYPE_BLACK, TYPE_PINK, TYPE_FREEBALL))
        assertThat(ballStack.availablePoints()).isEqualTo(19)

        ballStack.removeFreeBall() // Size 3 - RED()
        FrameToggles.FRAMETOGGLES.isFreeball = false
        assertThat(ballStack.availablePoints()).isEqualTo(13)

        ballStack.addNextBalls(4) // Size 7 - YELLOW()
        assertThat(ballStack.availablePoints()).isEqualTo(27)

        ballStack.addNextBalls(1) // Size 8 - COLOR()
        assertThat(ballStack.availablePoints()).isEqualTo(27)

        ballStack.addNextBalls(1) // Size 9 - RED()
        assertThat(ballStack.availablePoints()).isEqualTo(35)

        ballStack.addNextBalls(27) // Size 36 - COLOR()
        assertThat(ballStack.availablePoints()).isEqualTo(139)

        ballStack.addNextBalls(1) // Size 37 - RED()
        assertThat(ballStack.availablePoints()).isEqualTo(147)

        ballStack.addNextBalls(2) // Size 37 - RED()
        assertThat(ballStack.availablePoints()).isEqualTo(147)

        ballStack.addFreeBall(1) // Size 39 - FREE()
        assertThat(ballStack.availablePoints()).isEqualTo(155)

        ballStack.removeFreeBall() // Size 37 - RED()
        assertThat(ballStack.availablePoints()).isEqualTo(147)
    }

    @Test
    fun isThisBallColorAndNotLast_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()

        ballStack.addNextBalls(1) // Size 1 - NOBALL(), Not in colors
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(false)

        ballStack.addNextBalls(5) // Size 6 - COLOR(), but not last
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(false)

        ballStack.addNextBalls(4) // Size 10 - COLOR()
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(true)

        ballStack.addNextBalls(1) // Size 11 - RED()
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(false)

        ballStack.addNextBalls(24) // Size 35 - RED()
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(false)

        ballStack.addNextBalls(1) // Size 36 - last COLOR()
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(true)

        ballStack.addNextBalls(2) // Size 37 - Will not go above 37
        assertThat(ballStack.isThisBallColorAndNotLast()).isEqualTo(false)
    }

    @Test
    fun wasPreviousBallColour_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()

        ballStack.addNextBalls(1) // Size 1 - Out of range - NOBALL()
        assertThat(ballStack.wasPreviousBallColor()).isEqualTo(false)

        ballStack.addNextBalls(6) // Size 7 - YELLOW()
        assertThat(ballStack.wasPreviousBallColor()).isEqualTo(true)

        ballStack.addNextBalls(3) // Size 10 - COLOR()
        assertThat(ballStack.size).isEqualTo(10)
        assertThat(ballStack.wasPreviousBallColor()).isEqualTo(false)

        ballStack.addNextBalls(27) // Size 37 - RED() - possible with freeball
        assertThat(ballStack.wasPreviousBallColor()).isEqualTo(true)

        ballStack.addNextBalls(2) // Size 37 - Will not go above 37
        assertThat(ballStack.size).isEqualTo(37)
        assertThat(ballStack.wasPreviousBallColor()).isEqualTo(true)
    }

    @Test
    fun addNextBalls_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()

        ballStack.addNextBalls(0)
        assertThat(ballStack.lastOrNull()).isEqualTo(null)

        ballStack.addNextBalls(1) // Size 1 - NOBALL()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_NOBALL)

        ballStack.addNextBalls(2) // Size 3 - PINK()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_PINK)

        ballStack.addNextBalls(4) // Size 7 - YELLOW()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_YELLOW)

        ballStack.addNextBalls(1) // Size 8 - COLOR()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_COLOR)

        ballStack.addNextBalls(1) // Size 9 - RED()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_RED)

        ballStack.addNextBalls(27) // Size 36 - COLOR()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_COLOR)

        ballStack.addNextBalls(1) // Size 37 - RED()
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_RED)

        ballStack.addNextBalls(1) // Size > 37 - Out of range
        assertThat(ballStack.size).isEqualTo(37)
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_RED)

        ballStack.addNextBalls(5) // Size > 37 - Out of range
        assertThat(ballStack.size).isEqualTo(37)
        assertThat(ballStack.last().ballType).isEqualTo(TYPE_RED)
    }

    @Test
    fun addFreeBall_and_removeFreeBall_checks() {
        val ballStack: MutableList<DomainBall> = mutableListOf()

        ballStack.addNextBalls(3) // Size 3 - PINK()
        assertThat(ballStack.last().points).isEqualTo(ballStack.addFreeBall(1))
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL, TYPE_BLACK, TYPE_PINK, TYPE_FREEBALL))
        assertThat(ballStack.removeFreeBall()).isEqualTo(-ballStack.last().points)
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL, TYPE_BLACK, TYPE_PINK))

        ballStack.addNextBalls(4) // Size 7 - YELLOW()
        assertThat(ballStack.last().points).isEqualTo(ballStack.addFreeBall(1))
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL,
            TYPE_BLACK,
            TYPE_PINK,
            TYPE_BLUE,
            TYPE_BROWN,
            TYPE_GREEN,
            TYPE_YELLOW,
            TYPE_FREEBALL))
        assertThat(ballStack.removeFreeBall()).isEqualTo(-ballStack.last().points)
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL,
            TYPE_BLACK,
            TYPE_PINK,
            TYPE_BLUE,
            TYPE_BROWN,
            TYPE_GREEN,
            TYPE_YELLOW))

        ballStack.addNextBalls(2) // Size 9 - RED()
        assertThat(ballStack.addFreeBall(1)).isEqualTo(8)
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL,
            TYPE_BLACK,
            TYPE_PINK,
            TYPE_BLUE,
            TYPE_BROWN,
            TYPE_GREEN,
            TYPE_YELLOW,
            TYPE_COLOR,
            TYPE_RED,
            TYPE_COLOR,
            TYPE_FREEBALL))
        assertThat(ballStack.removeFreeBall()).isEqualTo(-8)
        assertThat(ballStack.ballTypeList()).isEqualTo(listOf(TYPE_NOBALL,
            TYPE_BLACK,
            TYPE_PINK,
            TYPE_BLUE,
            TYPE_BROWN,
            TYPE_GREEN,
            TYPE_YELLOW,
            TYPE_COLOR,
            TYPE_RED))
    }
}