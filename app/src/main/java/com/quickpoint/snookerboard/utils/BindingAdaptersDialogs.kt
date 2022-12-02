package com.quickpoint.snookerboard.utils

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.utils.MatchAction.*

@BindingAdapter("setDialogGameGenLabel")
fun TextView.setDialogGameGenLabel(matchAction: MatchAction) {
    text = when (matchAction) {
        MATCH_CANCEL -> "Cancel match"
        FRAME_RERACK -> "Rerack"
        FRAME_TO_END -> "Concede frame"
        MATCH_TO_END -> "Concede match"
        FRAME_ENDED -> "Frame ended"
        MATCH_ENDED -> "Match ended"
        FOUL_DIALOG -> "Foul"
        INFO_FOUL_DIALOG -> "Info foul"
        FRAME_RESPOT_BLACK -> "Re-spot black"
        FRAME_LOG_ACTIONS -> "Actions log"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenQuestion")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction) {
    text = when (matchAction) {
        MATCH_CANCEL -> "Are you sure you want to cancel the current match? All match progress will be lost."
        FRAME_RERACK -> "Are you sure you want to rerack? All frame progress will be lost."
        FRAME_TO_END -> "This frame is still in progress, are you sure you want to end it?"
        MATCH_TO_END -> "This match is still in progress, are you sure you want to end it?"
        FRAME_ENDED -> "This frame has mathematically ended. Would you like to proceed?"
        MATCH_ENDED -> "This match has mathematically ended. Would you like to proceed?"
        INFO_FOUL_DIALOG -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
        FRAME_RESPOT_BLACK -> "Looks like you and your opponent are tied at the end of the frame! The black ball will be re-spotted to decide the winner. The player who started the frame will attempt to pot the black first."
        FRAME_LOG_ACTIONS -> "If you've experienced any issues during this match you can submit an action log, which will be reviewed by the developer. Would you like to submit an action log?"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenAText")
fun TextView.setDialogGameAText(matchAction: MatchAction) {
    text = "No"
}

@BindingAdapter("dialogGameGenBText")
fun TextView.setDialogGameBText(matchAction: MatchAction) {
    text = when (matchAction) {
        MATCH_ENDED_DISCARD_FRAME -> "Yes & remove this frame"
        else -> ""
    }
}

@BindingAdapter("dialogGameGenCText", "dialogGameGenCActionB", "dialogGameGenCScore")
fun TextView.setDialogGameCText(matchAction: MatchAction, matchActionB: MatchAction, frame: DomainFrame?) {
    isVisible = !(matchActionB == MATCH_ENDED_DISCARD_FRAME && (frame?.score?.isFrameEqual() ?: false))
    text = when (matchAction) {
        INFO_FOUL_DIALOG, FRAME_RESPOT_BLACK -> "I understand"
        else -> "Yes"
    }
}

@BindingAdapter("dialogGameNote", "dialogGameNoteScore")
fun TextView.setDialogGameNote(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = if (matchAction == MATCH_ENDED_DISCARD_FRAME) View.VISIBLE else View.GONE
    text = when {
        frame == null -> ""
        frame.score.isNoFrameFinished() -> ""
        frame.score.isFrameWinResultingMatchTie() -> "NOTE: Keeping the current frame will result in a draw"
        frame.score.isMatchEqual() -> "NOTE: Discarding the current frame will result in a draw"
        else -> ""
    }
}