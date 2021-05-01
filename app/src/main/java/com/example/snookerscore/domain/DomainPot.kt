package com.example.snookerscore.domain

enum class PotType { HIT, FREE, SAFE, MISS, FOUL, REMOVERED, ADDRED }
enum class PotAction { CONTINUE, SWITCH }

sealed class DomainPot(
    val ball: DomainBall,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: DomainBall) : DomainPot(ball, PotType.HIT, PotAction.CONTINUE)
    object SAFE : DomainPot(DomainBall.NOBALL(), PotType.SAFE, PotAction.SWITCH)
    object MISS : DomainPot(DomainBall.NOBALL(), PotType.MISS, PotAction.SWITCH)
    object FREEMISS : DomainPot(DomainBall.NOBALL(), PotType.FREE, PotAction.CONTINUE)
    class FOUL(ball: DomainBall, action: PotAction) : DomainPot(ball, PotType.FOUL, action)
    object REMOVERED : DomainPot(DomainBall.NOBALL(), PotType.REMOVERED, PotAction.CONTINUE)
    object ADDRED : DomainPot(DomainBall.RED(), PotType.ADDRED, PotAction.CONTINUE)
}