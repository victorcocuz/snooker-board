package com.quickpoint.snookerboard.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.Constants
import com.quickpoint.snookerboard.core.utils.MatchAction
import com.quickpoint.snookerboard.domain.models.DomainScore
import com.quickpoint.snookerboard.domain.models.isFrameWinResultingMatchTie
import com.quickpoint.snookerboard.domain.models.isMatchEqual
import com.quickpoint.snookerboard.domain.models.isNoFrameFinished
import com.quickpoint.snookerboard.core.utils.MatchAction.*

@Composable
fun getGenericDialogTitleText(matchActionB: MatchAction, matchActionC: MatchAction) = stringResource(
    when {
        matchActionB == FRAME_MISS_FORFEIT -> R.string.d_generic_title_frame_miss_forfeit
        matchActionC == MATCH_CANCEL -> R.string.d_generic_title_match_cancel
        matchActionC == FRAME_RERACK -> R.string.d_generic_title_frame_rerack
        matchActionC == FRAME_TO_END -> R.string.d_generic_title_frame_to_end
        matchActionC == MATCH_TO_END -> R.string.d_generic_title_match_to_end
        matchActionC == FRAME_ENDED -> R.string.d_generic_title_frame_ended
        matchActionC == MATCH_ENDED -> R.string.d_generic_title_match_ended
        matchActionC == INFO_FOUL_DIALOG -> R.string.d_generic_title_info_foul_dialog
        matchActionC == FRAME_LAST_BLACK_FOULED -> R.string.d_generic_title_last_black_fouled
        matchActionC == FRAME_RESPOT_BLACK -> R.string.d_generic_title_frame_respot_black
        matchActionC == FRAME_LOG_ACTIONS -> R.string.d_generic_title_frame_log_actions
        matchActionC == FOUL_DIALOG -> R.string.d_generic_title_frame_foul
        else -> R.string.d_generic_not_implemented
    }, matchActionC
)

@Composable
fun getGenericDialogQuestionText(matchActionB: MatchAction, matchActionC: MatchAction) = stringResource(
    when {
        matchActionB == FRAME_MISS_FORFEIT -> R.string.d_generic_text_frame_miss_forfeit
        matchActionC == MATCH_CANCEL -> R.string.d_generic_text_match_cancel
        matchActionC == FRAME_RERACK -> R.string.d_generic_text_frame_rerack
        matchActionC == FRAME_TO_END -> R.string.d_generic_text_frame_to_end
        matchActionC == MATCH_TO_END -> R.string.d_generic_text_match_to_end
        matchActionC == FRAME_ENDED -> R.string.d_generic_text_frame_ended
        matchActionC == MATCH_ENDED -> R.string.d_generic_text_match_ended
        matchActionC == INFO_FOUL_DIALOG -> R.string.d_generic_text_info_foul_dialog
        matchActionC == FRAME_LAST_BLACK_FOULED -> R.string.d_generic_text_last_black_fouled
        matchActionC == FRAME_RESPOT_BLACK -> R.string.d_generic_text_frame_respot_black
        matchActionC == FRAME_LOG_ACTIONS -> R.string.d_generic_text_frame_log_actions
        else -> R.string.d_generic_not_implemented
    }, matchActionC
)

@Composable
fun getDialogGameNote(matchAction: MatchAction, score: List<DomainScore>?) = when {
    matchAction == FRAME_MISS_FORFEIT -> stringResource(R.string.d_generic_note_frame_miss_forfeit)
    score == null -> Constants.EMPTY_STRING
    score.isNoFrameFinished() -> Constants.EMPTY_STRING
    score.isFrameWinResultingMatchTie() -> stringResource(R.string.d_generic_note_match_tie_if_keep)
    score.isMatchEqual() -> stringResource(R.string.d_generic_note_mitch_tie_if_discard)
    else -> Constants.EMPTY_STRING
}

@Composable
fun getDialogGameCText(matchActionB: MatchAction, matchActionC: MatchAction) = stringResource(
    when {
        matchActionC in listOf(INFO_FOUL_DIALOG, FRAME_LAST_BLACK_FOULED, FRAME_RESPOT_BLACK) -> R.string.d_generic_answer_c_no_action
        matchActionB == FRAME_MISS_FORFEIT -> R.string.d_generic_answer_c_frame_miss_forfeit
        else -> R.string.d_generic_answer_c_generic
    }
)

@Composable
fun getDialogGameBText(matchActionB: MatchAction): String = when (matchActionB) {
    MATCH_ENDED_DISCARD_FRAME -> stringResource(R.string.d_generic_answer_b)
    else -> Constants.EMPTY_STRING
}