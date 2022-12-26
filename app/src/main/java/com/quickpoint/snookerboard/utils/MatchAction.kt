package com.quickpoint.snookerboard.utils

import android.content.Context
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

enum class MatchAction {
    // Foul Actions
    FOUL_ATTEMPT, // Attempt to submit a foul
    FOUL_CONFIRM, // Foul has been confirmed and will be processed
    FOUL_DIALOG, // Open foul dialog fragment

    // Rules Fragment Actions
    MATCH_PLAY, // When actioned from the play fragment
    INFO_FOUL_DIALOG, // On clicking the info foul button on the rules screen, it will open a generic dialog

    SNACK_NO_PLAYER, // Assign snackbar when player names are not fully completed
    SNACK_NO_FIRST, // Assign snackbar when no first player is selected
    SNACK_HANDICAP_FRAME_LIMIT,
    SNACK_HANDICAP_MATCH_LIMIT,

    // Game Fragment Match Actions
    MATCH_START_NEW, // When actioned from the game fragment, if no match exists in the db
    MATCH_CANCEL_DIALOG, // On clicking cancel match
    MATCH_CANCEL, // Action to cancel match
    MATCH_ENDING_DIALOG, // Open match ending dialog
    MATCH_ENDED_DISCARD_FRAME, // On clicking the conceding button when keeping/discarding current score can affect winner
    MATCH_TO_END, // On clicking the concede match button while the match is still in play
    MATCH_ENDED, // Match end has been confirmed and will be processed. On clicking the concede frame / match button after the frame is mathematically complete and if it is enough to win the game

    NAV_TO_POST_MATCH, // Last frame has been saved to repo so it's save to navigate to SummaryFragment

    // Game Fragment Frame Actions
    FRAME_START_NEW, // When actioned from endFrameOrMatch method in matchVm
    FRAME_RERACK_DIALOG, // On clicking rerack button
    FRAME_RERACK, // Action to reset frame
    FRAME_MISS_FORFEIT_DIALOG, // On rerack counter reaching value of 3
    FRAME_MISS_FORFEIT, // Person who missed 3 times in a row loses frame if he was not snookered
    FRAME_ENDING_DIALOG, // Open frame end dialog
    FRAME_TO_END, // On clicking the concede frame button while the frame is still ongoing
    FRAME_ENDED, // Frame end has been confirmed and will be processed. On clicking the concede frame button when the point difference is big enough, or automatically triggered when only one ball left
    FRAME_RESPOT_BLACK_DIALOG, // When both players are tied at the end of the frame
    FRAME_RESPOT_BLACK, // After the RESPOT_BLACK_DIALOG is closed, respot black
    FRAME_FREE_ACTIVE, // After a foul, uses observer to handle pot instead of directly from gameVm
    FRAME_UPDATED, // When frame updates are completed assign frame action that triggers the matchVm ot update DisplayScore
    FRAME_UNDO, // When triggered from gameVm execute as an action instead of recursive method
    FRAME_REMOVE_RED,
    FRAME_LOG_ACTIONS_DIALOG, // Opens a dialog allowing users to submit an action log
    FRAME_LOG_ACTIONS, // Submit an action log by e-mail to be tested and reviewed


    SNACK_UNDO,
    SNACK_ADD_RED,
    SNACK_REMOVE_COLOR,
    SNACK_FRAME_RERACK_DIALOG,
    SNACK_FRAME_ENDING_DIALOG,
    SNACK_MATCH_ENDING_DIALOG,
    SNACK_NO_BALL, // Assign snackbar when there are no balls on the table instead handling pot

    // Summary Fragment Actions
    NAV_TO_PLAY, // Go to main menu

    // Redundant Actions
    CLOSE_DIALOG, // Used when the action is to continue current state
    IGNORE, // Used when action should not be shown
    TRANSITION_TO_FRAGMENT, // Action to transition to fragment in a queue
}

val listOfMatchActionsUncancelable = listOf(MATCH_ENDED, FRAME_ENDED, FRAME_RESPOT_BLACK_DIALOG)


// Helper functions
fun MatchAction.getListOfDialogActions(isMatchEnding: Boolean, isNoFrameFinished: Boolean, isFrameMathematicallyOver: Boolean): List<MatchAction> = when (this) {
    FRAME_RESPOT_BLACK_DIALOG -> listOf(IGNORE, IGNORE, FRAME_RESPOT_BLACK)
    FRAME_RERACK_DIALOG -> listOf(CLOSE_DIALOG, IGNORE, FRAME_RERACK)
    FRAME_ENDING_DIALOG, MATCH_ENDING_DIALOG -> {
        val actionC = queryEndFrameOrMatch(isMatchEnding, isFrameMathematicallyOver)
        val actionB = if (actionC == MATCH_TO_END && !isNoFrameFinished) MATCH_ENDED_DISCARD_FRAME else IGNORE
        listOf(CLOSE_DIALOG, actionB, actionC)
    }
    MATCH_CANCEL_DIALOG -> listOf(CLOSE_DIALOG, IGNORE, MATCH_CANCEL)
    FRAME_LOG_ACTIONS_DIALOG -> listOf(CLOSE_DIALOG, IGNORE, FRAME_LOG_ACTIONS)
    FRAME_MISS_FORFEIT_DIALOG -> listOf(CLOSE_DIALOG, FRAME_MISS_FORFEIT, queryEndFrameOrMatch(isMatchEnding, isFrameMathematicallyOver))
    else -> listOf()
}

fun MatchAction.queryEndFrameOrMatch(
    isMatchEnding: Boolean,
    isFrameMathematicallyOver: Boolean,
): MatchAction { // When actioned from options menu or if last ball has been potted
    Timber.i("queryEndFrameOrMatch($this), isMatchEnding $isMatchEnding, isFrameMathematicallyOver $isFrameMathematicallyOver")
    return when { // Else assign a match action for a MATCH end query or else assign a FRAME ending action
        isMatchEnding -> { // If the frame would push player to win, assign a MATCH ending action
            if (isFrameMathematicallyOver) MATCH_ENDED
            else MATCH_TO_END
        }
        this == MATCH_ENDING_DIALOG -> MATCH_TO_END // Else assign a match action for a MATCH end query
        isFrameMathematicallyOver -> FRAME_ENDED // Else assign a FRAME ending action
        else -> FRAME_TO_END
    }
}

fun MatchAction.getPotType(): PotType? = when (this) {
    FRAME_RESPOT_BLACK -> TYPE_RESPOT_BLACK
    FRAME_FREE_ACTIVE -> TYPE_FREE_ACTIVE
    FRAME_REMOVE_RED -> TYPE_REMOVE_RED
    FOUL_CONFIRM -> TYPE_FOUL
    else -> null // For FRAME_UNDO
}

fun MatchAction.getSnackText(context: Context) = context.getString(
    when (this) {
        SNACK_NO_PLAYER -> R.string.snack_f_rules_no_player
        SNACK_NO_FIRST -> R.string.snack_f_rules_select_no_first
        SNACK_HANDICAP_FRAME_LIMIT -> R.string.snack_f_rules_handicap_frame_limit
        SNACK_HANDICAP_MATCH_LIMIT -> R.string.snack_f_rules_handicap_match_limit
        SNACK_UNDO -> R.string.snack_f_game_undo
        SNACK_ADD_RED -> R.string.snack_f_game_add_red
        SNACK_REMOVE_COLOR -> R.string.snack_f_game_remove_color
        SNACK_FRAME_RERACK_DIALOG -> R.string.snack_f_game_rerack
        SNACK_FRAME_ENDING_DIALOG -> R.string.snack_f_game_concede_frame
        SNACK_MATCH_ENDING_DIALOG -> R.string.snack_f_game_concede_match
        SNACK_NO_BALL -> R.string.toast_f_game_no_balls_left
        else -> R.string.empty
    }
)