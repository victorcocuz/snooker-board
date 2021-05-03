package com.example.snookerscore.domain


data class DomainRanking(
    var position: Int = 0,
    val name: String = "",
    val points: Int = 0
)

enum class BallAdapterType { MATCH, FOUL, BREAK }

enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS}

enum class MatchAction {
    FOUL_QUERIED,
    FOUL_CONFIRMED,
    FRAME_END_QUERY,
    FRAME_END_CONFIRM,
    MATCH_END_QUERY,
    MATCH_END_CONFIRM,
    MATCH_CONTINUE,
    MATCH_START_NEW,
    MATCH_CANCEL,
    NO_ACTION
}
