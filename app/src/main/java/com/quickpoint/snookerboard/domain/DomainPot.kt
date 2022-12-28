package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.ShotType.STANDARD
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS

// Classes and variables that define all pot types and pot actions
enum class PotType { TYPE_HIT, TYPE_FOUL, TYPE_FREE, TYPE_FOUL_ATTEMPT, TYPE_SNOOKER, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_MISS, TYPE_FREE_ACTIVE, TYPE_REMOVE_RED, TYPE_REMOVE_COLOR, TYPE_ADDRED, TYPE_RESPOT_BLACK }
enum class ShotType { STANDARD, LONG, REST, LONG_AND_REST }

val listOfPotTypesHelpers = listOf(TYPE_FREE_ACTIVE, TYPE_RESPOT_BLACK)
val listOfPotTypesPointsAdding = listOf(TYPE_HIT, TYPE_FREE, TYPE_ADDRED)
val listOfPotTypesPointGenerating = listOfPotTypesPointsAdding.plus(TYPE_FOUL)
val listOfPotTypesForNoBallSnackbar = listOf(TYPE_HIT, TYPE_MISS, TYPE_SAFE, TYPE_SAFE_MISS, TYPE_SNOOKER, TYPE_FOUL, TYPE_FOUL_ATTEMPT)

enum class PotAction { FIRST, SWITCH, CONTINUE, RETAKE }

// All game logic is based on DOMAIN Pots. Every shot that happens is defined within the constraints below
sealed class DomainPot(
    var potId: Long,
    val ball: DomainBall,
    val potType: PotType,
    val potAction: PotAction,
    val shotType: ShotType
) {
    class HIT(potId: Long = 0, ball: DomainBall, shotType: ShotType) : DomainPot(potId, ball, TYPE_HIT, CONTINUE, shotType) // Ball can vary
    class FOUL(potId: Long = 0, ball: DomainBall, action: PotAction, shotType: ShotType) : DomainPot(potId, ball, TYPE_FOUL, action, shotType) // Ball and action can vary
    class FOULATTEMPT(potId: Long = 0, shotType: ShotType) : DomainPot(potId, NOBALL(), TYPE_FOUL_ATTEMPT, CONTINUE, shotType) // Foul will only be validated if there are balls left on the table
    class FREEACTIVE(potId: Long = 0) : DomainPot(potId, FREEBALLTOGGLE(), TYPE_FREE_ACTIVE, CONTINUE, STANDARD) // Static action for a miss on a free ball
    class FREE(potId: Long = 0, ball: DomainBall, shotType: ShotType) : DomainPot(potId, ball, TYPE_FREE, CONTINUE, shotType) // Ball should only be FREEBALL(), but points may vary
    class SNOOKER(potId: Long = 0, shotType: ShotType) : DomainPot(potId, NOBALL(), TYPE_SNOOKER, SWITCH, shotType) // Static action for a successful snooker shot
    class SAFE(potId: Long = 0, shotType: ShotType) : DomainPot(potId, NOBALL(), TYPE_SAFE, SWITCH, shotType) // Static action for a safety shot
    class SAFEMISS(potId: Long = 0, shotType: ShotType) : DomainPot(potId, NOBALL(), TYPE_SAFE_MISS, SWITCH, shotType) // Static action for a missed safety shot
    class MISS(potId: Long = 0, shotType: ShotType) : DomainPot(potId, NOBALL(), TYPE_MISS, SWITCH, shotType) // Static action for a missed shot
    class REMOVERED(potId: Long = 0) : DomainPot(potId, RED(), TYPE_REMOVE_RED, CONTINUE, STANDARD) // Static action for removing a red ball
    class REMOVECOLOR(potId: Long = 0) : DomainPot(potId, COLOR(), TYPE_REMOVE_COLOR, CONTINUE, STANDARD) // Static action for removing a red ball
    class ADDRED(potId: Long = 0) : DomainPot(potId, RED(), TYPE_ADDRED, CONTINUE, STANDARD) // Static action for adding a red ball (when more than one red is sunk at once)
    class RESPOTBLACK(potId: Long = 0) : DomainPot(potId, NOBALL(), TYPE_RESPOT_BLACK, FIRST, STANDARD) // Static action for re-spotting black ball when players are tied

    fun getActionLog(description: String, lastBall: BallType?, size: Int): DomainActionLog {
        return DomainActionLog(
            description = description,
            potType = potType,
            ballType = ball.ballType,
            ballPoints = ball.points,
            potAction = potAction,
            player = SETTINGS.crtPlayer,
            breakCount = size,
            ballStackLast = lastBall,
            frameCount = SETTINGS.crtFrame
        )
    }
}

fun PotType.getPotFromType(ball: DomainBall = NOBALL(), action: PotAction = CONTINUE, shotType: ShotType) = when (this) {
    TYPE_HIT -> HIT((SETTINGS.assignUniqueId()), ball, shotType)
    TYPE_FOUL -> FOUL((SETTINGS.assignUniqueId()), ball, action, shotType)
    TYPE_FREE -> FREE(SETTINGS.assignUniqueId(), ball, shotType)
    TYPE_FOUL_ATTEMPT -> FOULATTEMPT(SETTINGS.assignUniqueId(), shotType)
    TYPE_SNOOKER -> SNOOKER(SETTINGS.assignUniqueId(), shotType)
    TYPE_SAFE -> SAFE(SETTINGS.assignUniqueId(), shotType)
    TYPE_SAFE_MISS -> SAFEMISS(SETTINGS.assignUniqueId(), shotType)
    TYPE_MISS -> MISS(SETTINGS.assignUniqueId(), shotType)
    TYPE_FREE_ACTIVE -> FREEACTIVE(SETTINGS.assignUniqueId())
    TYPE_REMOVE_RED -> REMOVERED(SETTINGS.assignUniqueId())
    TYPE_REMOVE_COLOR -> REMOVECOLOR(SETTINGS.assignUniqueId())
    TYPE_ADDRED -> ADDRED(SETTINGS.assignUniqueId())
    TYPE_RESPOT_BLACK -> RESPOTBLACK(SETTINGS.assignUniqueId())
}

