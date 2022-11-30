package com.quickpoint.snookerboard.domain

import com.quickpoint.snookerboard.utils.asText
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
    fun asText() = Timber.i("${description}${potType.asText()}${ballType.asText()}${ballPoints.asText()}${potAction.asText()}${player.asText()}${breakCount.asText()}${ballStackLast.asText()}${frameCount.asText()}")
}

fun MutableList<DomainActionLog>.addLog(actionLog: DomainActionLog) {
    add(actionLog)
    actionLog.asText()
}