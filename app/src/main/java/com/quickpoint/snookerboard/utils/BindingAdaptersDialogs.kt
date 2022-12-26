package com.quickpoint.snookerboard.utils

import android.view.View
import android.view.View.*
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.slider.Slider
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.utils.MatchAction.*

@BindingAdapter("dialogGameGenLabel", "dialogGameGenLabelActionB")
fun TextView.setDialogGameGenLabel(matchAction: MatchAction, matchActionB: MatchAction) {
    text = when {
        matchActionB == FRAME_MISS_FORFEIT -> "Forfeit frame"
        matchAction == MATCH_CANCEL -> "Cancel match"
        matchAction == FRAME_RERACK -> "Rerack"
        matchAction == FRAME_TO_END -> "Concede frame"
        matchAction == MATCH_TO_END -> "Concede match"
        matchAction == FRAME_ENDED -> "Frame ended"
        matchAction == MATCH_ENDED -> "Match ended"
        matchAction == FOUL_DIALOG -> "Foul"
        matchAction == INFO_FOUL_DIALOG -> "Info foul"
        matchAction == FRAME_RESPOT_BLACK -> "Re-spot black"
        matchAction == FRAME_LOG_ACTIONS -> "Actions log"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenQuestion", "dialogGameGenQuestionActionB")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction, matchActionB: MatchAction) {
    text = when {
        matchActionB == FRAME_MISS_FORFEIT -> "If a player commits Foul and a Miss three times in a row while the object ball is fully visible, this results in the frame being automatically lost. If this is the case, please choose to forfeit frame."
        matchAction == MATCH_CANCEL -> "Are you sure you want to cancel the current match? All match progress will be lost."
        matchAction == FRAME_RERACK -> "Are you sure you want to rerack? All frame progress will be lost."
        matchAction == FRAME_TO_END -> "This frame is still in progress, are you sure you want to end it?"
        matchAction == MATCH_TO_END -> "This match is still in progress, are you sure you want to end it?"
        matchAction == FRAME_ENDED -> "This frame has mathematically ended. Would you like to proceed?"
        matchAction == MATCH_ENDED -> "This match has mathematically ended. Would you like to proceed?"
        matchAction == INFO_FOUL_DIALOG -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
        matchAction == FRAME_RESPOT_BLACK -> "Looks like you and your opponent are tied at the end of the frame! The black ball will be re-spotted to decide the winner. The player who started the frame will attempt to pot the black first."
        matchAction == FRAME_LOG_ACTIONS -> "If you've experienced any issues during this match you can submit an action log, which will be reviewed by the developer. Would you like to submit an action log?"
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
    text = when {
        matchAction in listOf(INFO_FOUL_DIALOG, FRAME_RESPOT_BLACK) -> "I understand"
        matchActionB == FRAME_MISS_FORFEIT -> "Yes, forfeit the frame"
        else -> "Yes"
    }
}

@BindingAdapter("dialogGameNote", "dialogGameNoteScore")
fun TextView.setDialogGameNote(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = if (matchAction in listOf(MATCH_ENDED_DISCARD_FRAME, FRAME_MISS_FORFEIT)) VISIBLE else GONE
    text = when {
        matchAction == FRAME_MISS_FORFEIT -> "NOTE: Please read carefully, this action cannot be undone"
        frame == null -> ""
        frame.score.isNoFrameFinished() -> ""
        frame.score.isFrameWinResultingMatchTie() -> "NOTE: Keeping the current frame will result in a draw"
        frame.score.isMatchEqual() -> "NOTE: Discarding the current frame will result in a draw"
        else -> ""
    }
}

@BindingAdapter("setRemoveRedsRange")
fun Slider.setRemoveRedsRange(ballStack: MutableList<DomainBall>) {
    valueTo = ballStack.maxRemoveReds().toFloat()
    visibility = if (ballStack.maxRemoveReds() > 0) VISIBLE else GONE
}


@BindingAdapter("setRemoveRedsVisibility", "setRemoveRedsVisibilityRedsDesired")
fun View.setRemoveRedsVisibility(ballStack: MutableList<DomainBall>, redsDesired: Int) {
    visibility = if (ballStack.maxRemoveReds() >= redsDesired) VISIBLE else GONE
}

@BindingAdapter("onChangeListener")
fun Slider.onChangeListener(function: (Int) -> Unit) {
    addOnChangeListener { slider, value, fromUser ->
        function(value.toInt())
    }
}