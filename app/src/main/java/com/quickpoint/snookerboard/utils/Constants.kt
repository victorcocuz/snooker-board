package com.quickpoint.snookerboard.utils

const val BALL_HEIGHT_FACTOR_MATCH_ACTION = 7

enum class BallAdapterType { MATCH, FOUL, BREAK }
enum class FrameEvent {HANDLE_POT, HANDLE_FOUL, HANDLE_UNDO}
enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS}
enum class PlayerTagType {MATCH, STATISTICS} // Create different player tags for display during match and for statistics

enum class MatchAction {
    // Foul Actions
    FOUL_DIALOG_QUERIED, // Open foul dialog fragment
    FOUL_QUERIED, // Attempt to submit a foul
    FOUL_CONFIRMED, // Foul has been confirmed and will be processed

    // Frame Actions
    FRAME_UPDATE_RECORD, // Update frame score and history
    FRAME_RESET, // Action to reset frame
    FRAME_RESET_COMPLETE, // Action that confirms the frame has been reset
    FRAME_END_QUERIED, // On clicking the concede frame button while the frame is still ongoing
    FRAME_END_CONFIRMED, // Frame end has been confirmed and will be processed. On clicking the concede frame button when the point difference is big enough, or automatically triggered when only one ball left

    // Match Actions
    MATCH_START, // Action that starts match
    MATCH_RESET, // Action to reset match rules when starting a new match or on game cancel
    MATCH_END_QUERIED, // On clicking the concede match button while the match is still in play
    MATCH_END_CONFIRM_AND_DISCARD_CRT_FRAME, // On clicking the conceding button when the current play can affect who wins
    MATCH_END_CONFIRMED, // Match end has been confirmed and will be processed. On clicking the concede frame / match button after the frame is mathematically complete and if it is enough to win the game
    MATCH_LOAD, // On confirming the dialog at the beginning that you wish to continue match
    MATCH_LOAD_TO_GAME_VM, // Action to continue the loading to the game view model
    MATCH_START_NEW, // On confirming the dialog at the beginning that you wish to start a new match, or if there are no matches in progress
    MATCH_CANCEL, // On clicking cancel match

    // Other
    APP_TO_MAIN, // Go to main menu
    INFO_FOUL, // On clicking the info foul button on the rules screen, it will open a generic dialog

    // Redundant Actions
    NO_ACTION, // Used for a void action
    IGNORE // Cannot remember why I used both
}