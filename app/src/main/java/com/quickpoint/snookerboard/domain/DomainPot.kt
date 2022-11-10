package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.domain.PotType.*

// Classes and variables that define all pot types and pot actions
enum class PotType { TYPE_HIT, TYPE_FOUL, TYPE_SNOOKER, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_MISS, TYPE_FREE, TYPE_FREEAVAILABLE, TYPE_FREETOGGLE, TYPE_REMOVERED, TYPE_ADDRED, TYPE_RESPOT_BLACK }
val listOfExtraActions = listOf(TYPE_FOUL, TYPE_SNOOKER, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_MISS, TYPE_REMOVERED)
val listOfPointGeneratingPotTypes = listOf(TYPE_HIT, TYPE_FOUL, TYPE_FREE, TYPE_ADDRED)
val listOfBreakPotTypes = listOf(TYPE_HIT, TYPE_FOUL, TYPE_FREE, TYPE_ADDRED, TYPE_REMOVERED, TYPE_FOUL, TYPE_SNOOKER, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_MISS)
val listOfHelperPotTypes = listOf(TYPE_FREEAVAILABLE, TYPE_FREETOGGLE, TYPE_RESPOT_BLACK)

enum class PotAction { FIRST, CONTINUE, SWITCH }

// All game logic is based on DOMAIN Pots. Every shot that happens is defined within the constraints below
sealed class DomainPot(
    val ball: DomainBall,
    val potType: PotType,
    val potAction: PotAction
) {
    class HIT(ball: DomainBall) : DomainPot(ball, TYPE_HIT, CONTINUE) // Ball can vary
    class FOUL(ball: DomainBall, action: PotAction) : DomainPot(ball, TYPE_FOUL, action) // Ball and action can vary
    object FOULATTEMPT : DomainPot(NOBALL(), TYPE_FOUL, CONTINUE) // Static action for a foul attempt - foul will only be validated if there are balls left on the table
    object SNOOKER : DomainPot(NOBALL(), TYPE_SNOOKER, SWITCH) // Static action for a successful snooker shot
    object SAFE : DomainPot(NOBALL(), TYPE_SAFE, SWITCH) // Static action for a safety shot
    object SAFEMISS : DomainPot(NOBALL(), TYPE_SAFE_MISS, SWITCH) // Static action for a missed safety shot
    object MISS : DomainPot(NOBALL(), TYPE_MISS, SWITCH) // Static action for a missed shot
    object FREE : DomainPot(FREEBALL(), TYPE_FREE, CONTINUE) // Static action for potting a free ball
    object FREEAVAILABLE : DomainPot(FREEBALLAVAILABLE(), TYPE_FREEAVAILABLE, CONTINUE)
    object FREETOGGLE : DomainPot(FREEBALLTOGGLE(), TYPE_FREETOGGLE, CONTINUE) // Static action for a miss on a free ball
    object REMOVERED : DomainPot(RED(), TYPE_REMOVERED, CONTINUE) // Static action for removing a red ball
    object ADDRED : DomainPot(RED(), TYPE_ADDRED, CONTINUE) // Static action for adding a red ball (i.e. when more than one red is sunk at once)
    object RESPOTBLACK : DomainPot(NOBALL(),TYPE_RESPOT_BLACK, FIRST) // Static action for re-spotting black ball when players are tied

fun isFreeballAvailable() = this.potType == TYPE_FOUL && this.potAction == SWITCH
}