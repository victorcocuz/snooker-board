package com.quickpoint.snookerboard.domain

// Classes that define all pot types and pot actions
enum class PotType { HIT, FREE, SAFE, MISS, FOUL, REMOVERED, ADDRED }
enum class PotAction { CONTINUE, SWITCH }

// All game logic is based on DOMAIN Pots. Every shot that happens is defined within the constraints below
sealed class DomainPot(
    val ball: DomainBall,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: DomainBall) : DomainPot(ball, PotType.HIT, PotAction.CONTINUE) // Ball can vary
    class FOUL(ball: DomainBall, action: PotAction) : DomainPot(ball, PotType.FOUL, action) // Ball and action can vary
    object SAFE : DomainPot(DomainBall.NOBALL(), PotType.SAFE, PotAction.SWITCH) // Static action for a safe shot
    object MISS : DomainPot(DomainBall.NOBALL(), PotType.MISS, PotAction.SWITCH) // Static action for a missed shot
    object FREEMISS : DomainPot(DomainBall.NOBALL(), PotType.FREE, PotAction.CONTINUE) // Static action for a miss on a free ball
    object REMOVERED : DomainPot(DomainBall.NOBALL(), PotType.REMOVERED, PotAction.CONTINUE) // Static action for removing a red ball
    object ADDRED : DomainPot(DomainBall.RED(), PotType.ADDRED, PotAction.CONTINUE) // Static action for adding a red ball (i.e. when more than one red is sunk at once)
}