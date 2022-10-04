package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.domain.PotType.*

// Classes that define all pot types and pot actions
enum class PotType { TYPE_HIT, TYPE_FREE, TYPE_FREEAVAILABLE, TYPE_FREETOGGLE, TYPE_SAFE, TYPE_MISS, TYPE_FOUL, TYPE_REMOVERED, TYPE_ADDRED }
enum class PotAction { CONTINUE, SWITCH }

// All game logic is based on DOMAIN Pots. Every shot that happens is defined within the constraints below
sealed class DomainPot(
    val ball: DomainBall,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: DomainBall) : DomainPot(ball, TYPE_HIT, CONTINUE) // Ball can vary
    class FOUL(ball: DomainBall, action: PotAction) : DomainPot(ball, TYPE_FOUL, action) // Ball and action can vary
    object SAFE : DomainPot(DomainBall.NOBALL(), TYPE_SAFE, SWITCH) // Static action for a safe shot
    object MISS : DomainPot(DomainBall.NOBALL(), TYPE_MISS, SWITCH) // Static action for a missed shot
    object FREE : DomainPot(DomainBall.FREEBALL(), TYPE_FREE, CONTINUE) // Static action for potting a free ball
    object FREEAVAILABLE : DomainPot(DomainBall.FREEBALLAVAILABLE(), TYPE_FREEAVAILABLE, CONTINUE)
    object FREETOGGLE : DomainPot(DomainBall.FREEBALLTOGGLE(), TYPE_FREETOGGLE, CONTINUE) // Static action for a miss on a free ball
    object REMOVERED : DomainPot(DomainBall.RED(), TYPE_REMOVERED, CONTINUE) // Static action for removing a red ball
    object ADDRED : DomainPot(DomainBall.RED(), TYPE_ADDRED, CONTINUE) // Static action for adding a red ball (i.e. when more than one red is sunk at once)

fun isFreeballAvailable() = this.potType == TYPE_FOUL && this.potAction == SWITCH
}