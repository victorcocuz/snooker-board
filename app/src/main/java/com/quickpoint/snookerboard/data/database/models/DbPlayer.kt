package com.quickpoint.snookerboard.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.domain.models.DomainPlayer

@Entity(tableName = SnookerDatabase.TABLE_PLAYER)
data class DbPlayer(
    @PrimaryKey(autoGenerate = false)
    val playerId: Long,
    var firstName: String,
    var lastName: String,
)

fun List<DbPlayer>.asDomain(): MutableList<DomainPlayer> {
    return map {
        DomainPlayer(
            playerId = it.playerId,
            firstName = it.firstName,
            lastName = it.lastName
        )
    }.toMutableList()
}