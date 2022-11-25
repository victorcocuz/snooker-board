package com.quickpoint.snookerboard.domain

import timber.log.Timber

data class DomainActionLog(
    val description: String,
    val potType: PotType? = null,
    val ballType: BallType? = null,
    val ballPoints: Int? = null,
    val potAction: PotAction? = null,
    val player: Int? = null,
    val breakCount: Int? = null,
    val ballStackLast: BallType? = null,
    val frameCount: Long? = null
) {
    fun asText() = Timber.i("$description, potType: ${potType ?: "null"}, ballType: ${ballType ?: "null"}, ballPoints: ${ballPoints ?: "null"}, potAction: ${potAction ?: "null"}, player: ${player ?: "null"}, breakCount: ${breakCount  ?: "null"}, lastInBallStack: ${ballStackLast ?: "null"}, frame: ${frameCount ?: "null"}")
}