package com.quickpoint.snookerboard.utils

const val BALL_HEIGHT_FACTOR_MATCH_ACTION = 7

enum class BallAdapterType { MATCH, FOUL, BREAK }

enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS}

enum class FrameEvent {HANDLE_POT, HANDLE_FOUL, HANDLE_UNDO}

enum class PlayerTagType {MATCH, STATISTICS}

enum class MatchAction {
    FOUL_QUERIED,
    FOUL_CONFIRMED,
    FRAME_END_QUERY,
    FRAME_END_CONFIRM,
    MATCH_START,
    MATCH_END_QUERY,
    MATCH_END_CONFIRM_DISCARD,
    MATCH_END_CONFIRM,
    MATCH_RELOAD,
    MATCH_START_NEW,
    MATCH_CANCEL,
    NAME_CHANGE_A_QUERIED,
    NAME_CHANGE_B_QUERIED,
    APP_TO_MAIN,
    INFO_FOUL,
    NO_ACTION,
    IGNORE
}