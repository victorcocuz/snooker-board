package com.quickpoint.snookerboard.utils

const val BALL_HEIGHT_FACTOR_MATCH_ACTION = 7

enum class BallAdapterType { MATCH, FOUL, BREAK }
enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS }
enum class PlayerTagType { MATCH, STATISTICS } // Create different player tags for display during match and for statistics

enum class MatchAction {
    // Foul Actions
    FOUL_QUERY, // Attempt to submit a foul
    FOUL_CONFIRM, // Foul has been confirmed and will be processed
    FOUL_DIALOG, // Open foul dialog fragment

    // Frame Actions
    FRAME_START_NEW,
    FRAME_RERACK_DIALOG, // On clicking rerack button
    FRAME_RERACK, // Action to reset frame
    FRAME_ENDED_DIALOG, // Open frame end dialog
    FRAME_TO_END_DIALOG, // On clicking the concede frame button while the frame is still ongoing
    FRAME_ENDED, // Frame end has been confirmed and will be processed. On clicking the concede frame button when the point difference is big enough, or automatically triggered when only one ball left

    // Match Actions
    MATCH_PLAY, // When actioned from the play fragment
    MATCH_START_NEW, // When actioned from the game fragment, if no match exists in the db
    MATCH_LOAD, // When actioned from the game fragment, will load game from db
    MATCH_CANCEL_DIALOG, // On clicking cancel match
    MATCH_CANCEL, // Action to cancel match
    MATCH_ENDED_DIALOG, // Open match ending dialog
    MATCH_ENDED_DISCARD_FRAME_DIALOG, // On clicking the conceding button when keeping/discarding current score can affect winner
    MATCH_TO_END_DIALOG, // On clicking the concede match button while the match is still in play
    MATCH_ENDED, // Match end has been confirmed and will be processed. On clicking the concede frame / match button after the frame is mathematically complete and if it is enough to win the game
    GO_TO_POST_MATCH, // Last frame has been saved to repo so it's save to navigate to FragmentPostGame

    // Other
    NAVIGATE_HOME, // Go to main menu
    INFO_FOUL_DIALOG, // On clicking the info foul button on the rules screen, it will open a generic dialog

    // Redundant Actions
    CLOSE_DIALOG, // Used when the action is to continue current state
    IGNORE // Used when action should not be shown
}