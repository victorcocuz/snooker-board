package com.quickpoint.snookerboard.network

import com.quickpoint.snookerboard.database.DatabaseRanking
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkRankingContainer(val rankings: List<NetworkRanking>)

@JsonClass(generateAdapter = true)
data class NetworkRanking(
    @Json(name = "Position") val position: Int,
    @Json(name = "PlayerID") var id: Int,
    @Json(name = "Sum") val points: Int
)

@JsonClass(generateAdapter = true)
data class NetworkPlayer(
    @Json(name = "ID") val id: Int,
    @Json(name = "FirstName") val firstName: String,
    @Json(name = "LastName") val lastName: String,
)

fun NetworkRankingContainer.asDatabaseModel(listPlayers: List<NetworkPlayer>): Array<DatabaseRanking> {
    return rankings.map { networkRanking ->
        DatabaseRanking(
            position = networkRanking.position,
            name = listPlayers.find { it.id == networkRanking.id }?.firstName + " " + listPlayers.find { it.id == networkRanking.id }?.lastName,
            points = networkRanking.points
        )
    }.toTypedArray()
}