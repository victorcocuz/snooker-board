package com.example.snookerscore.network

import com.example.snookerscore.database.DatabaseRanking
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkRankingContainer(val rankings: List<NetworkRanking>)

@JsonClass(generateAdapter = true)
data class NetworkRanking(
    @Json(name = "Position") val position: Int,
    @Json(name = "PlayerID") var id: String,
    @Json(name = "Sum") val points: String
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
            name = listPlayers.find { it.id == networkRanking.id.toInt() }?.firstName + " " + listPlayers.find { it.id == networkRanking.id.toInt() }?.lastName,
            points = networkRanking.position
        )
    }.toTypedArray()
}