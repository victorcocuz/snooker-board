package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.core.utils.asText
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
)

fun MutableList<DomainActionLog>.addLog(actionLog: DomainActionLog) {
    add(actionLog)
    Timber.i(actionLog.asText())
}
