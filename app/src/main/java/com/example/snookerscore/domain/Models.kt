package com.example.snookerscore.domain


data class DomainRanking(
    var position: Int = 0,
    val name: String = "",
    val points: Int = 0
)

enum class BallAdapterType { MATCH, FOUL, BREAK }