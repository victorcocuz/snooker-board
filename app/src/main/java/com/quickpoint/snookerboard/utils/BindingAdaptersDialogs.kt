package com.quickpoint.snookerboard.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.quickpoint.snookerboard.domain.DomainFrame

@BindingAdapter("setDialogGameGenLabel")
fun TextView.setDialogGameGenLabel(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Cancel match"
        MatchAction.FRAME_RERACK -> "Rerack"
        MatchAction.FRAME_TO_END -> "Concede frame"
        MatchAction.MATCH_TO_END -> "Concede match"
        MatchAction.FRAME_ENDED -> "Frame ended"
        MatchAction.MATCH_ENDED -> "Match ended"
        MatchAction.FOUL_DIALOG -> "Foul"
        MatchAction.INFO_FOUL_DIALOG -> "Info foul"
        MatchAction.FRAME_RESPOT_BLACK -> "Re-spot black"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenQuestion")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Are you sure you want to cancel the current match? All match progress will be lost."
        MatchAction.FRAME_RERACK -> "Are you sure you want to rerack? All frame progress will be lost."
        MatchAction.FRAME_TO_END -> "This frame is still in progress, are you sure you want to end it?"
        MatchAction.MATCH_TO_END -> "This match is still in progress, are you sure you want to end it?"
        MatchAction.FRAME_ENDED -> "This frame has ended. Would you like to proceed?"
        MatchAction.MATCH_ENDED -> "This match has ended. Would you like to proceed?"
        MatchAction.INFO_FOUL_DIALOG -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
        MatchAction.FRAME_RESPOT_BLACK -> "Looks like you and your opponent are tied at the end of the frame! The black ball will be re-spotted to decide the winner. The player who started the frame will attempt to pot the black first."
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenA")
fun TextView.setDialogGameA(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "No"
        MatchAction.FRAME_RERACK -> "No"
        MatchAction.FRAME_TO_END -> "No"
        MatchAction.MATCH_TO_END -> "No"
        MatchAction.FRAME_ENDED -> "No"
        MatchAction.MATCH_ENDED -> "No"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenB", "dialogGameGenBScore")
fun TextView.setDialogGameB(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = when {
        frame == null -> View.GONE
        frame.isNoFrameFinished() -> View.GONE
        matchAction == MatchAction.MATCH_ENDED_DISCARD_FRAME -> View.VISIBLE
        else -> View.GONE
    }
    text = when (matchAction) {
        MatchAction.MATCH_ENDED_DISCARD_FRAME -> "Yes & Remove Frame"
        else -> ""
    }
}

@BindingAdapter("dialogGameGenC", "dialogGameGenCActionB", "dialogGameGenCScore")
fun TextView.setDialogGameC(matchAction: MatchAction, matchActionB: MatchAction, frame: DomainFrame?) {
    isEnabled = !(matchActionB == MatchAction.MATCH_ENDED_DISCARD_FRAME && (frame?.isFrameEqual() ?: false)) // This needs adjusting.
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Yes"
        MatchAction.FRAME_RERACK -> "Yes"
        MatchAction.FRAME_TO_END -> "Yes"
        MatchAction.MATCH_TO_END -> "Yes"
        MatchAction.FRAME_ENDED -> "Yes"
        MatchAction.MATCH_ENDED -> "Yes"
        MatchAction.FRAME_RESPOT_BLACK -> "OK"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameNote", "dialogGameNoteScore")
fun TextView.setDialogGameNote(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = if (matchAction == MatchAction.MATCH_ENDED_DISCARD_FRAME) View.VISIBLE else View.GONE
    text = when {
        frame == null -> ""
        frame.isNoFrameFinished() -> ""
        frame.isFrameWinResultingInMatchTie() -> "NOTE: Keeping the current frame will result in a draw"
        frame.isMatchEqual() -> "NOTE: Discarding the current frame will result in a draw"
        else -> ""
    }
}