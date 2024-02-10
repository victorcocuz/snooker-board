package com.quickpoint.snookerboard.domain.models

import com.quickpoint.snookerboard.core.utils.Constants
import com.quickpoint.snookerboard.data.database.models.DbPlayer

data class DomainPlayer(
    val playerId: Long,
    var firstName: String,
    var lastName: String,
)

fun List<DomainPlayer>.asDbPlayer(): List<DbPlayer> {
    return map { domainPlayer ->
        DbPlayer(
            playerId = domainPlayer.playerId,
            firstName = domainPlayer.firstName,
            lastName = domainPlayer.lastName
        )
    }
}

fun DomainPlayer.asDbPlayer() = DbPlayer(
    playerId = playerId,
    firstName = firstName,
    lastName = lastName
)

fun DomainPlayer.hasNoName() = firstName == Constants.EMPTY_STRING || lastName == Constants.EMPTY_STRING
