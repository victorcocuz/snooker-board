package com.example.snookerscore.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.snookerscore.R
import com.example.snookerscore.fragments.game.*
import kotlin.math.abs

// Ball Item View
@BindingAdapter("ballValue", "stackSize")
fun TextView.setPointsValue(item: Ball?, stackSize: Int) {
    val reds = (stackSize - 7) / 2
    item?.let {
        if (reds > 0 && it.ballType == BallType.RED) text = reds.toString()
    }
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: Ball?) {
    item?.let {
        setBackgroundResource(
            when (item) {
                Balls.RED -> R.drawable.ball_red
                Balls.YELLOW -> R.drawable.ball_yellow
                Balls.GREEN -> R.drawable.ball_green
                Balls.BROWN -> R.drawable.ball_brown
                Balls.BLUE -> R.drawable.ball_blue
                Balls.PINK -> R.drawable.ball_pink
                Balls.BLACK -> R.drawable.ball_black
                else -> R.drawable.ball_white
            }
        )
    }
}

// Game Display
@BindingAdapter("crtPlayerA")
fun TextView.setCurrentPlayerA(crtPlayer: CurrentPlayer) {
    setBackgroundColor(
        when (crtPlayer) {
            crtPlayer.getFirst() -> ContextCompat.getColor(context, R.color.design_default_color_primary)
            else -> 0x00000000
        }
    )
}

@BindingAdapter("crtPlayerB")
fun TextView.setCurrentPlayerB(crtPlayer: CurrentPlayer) {
    setBackgroundColor(
        when (crtPlayer) {
            crtPlayer.getFirst() -> 0x00000000
            else -> ContextCompat.getColor(context, R.color.design_default_color_primary)
        }
    )
}

@BindingAdapter("setTotalScore")
fun TextView.setTotalScore(frames: Int) {
    text = context.getString(R.string.game_total_score, (frames * 2 - 1))
}

@BindingAdapter("gamePointsDiffPlayer", "gamePointsDiffSize")
fun TextView.setGamePointsRemaining(crtPlayer: CurrentPlayer, size: Int) {
    text = if (size <= 7) ((-(8 - size) * (8 - size) - (8 - size) + 56) / 2).toString()
    else (27 + ((size - 7) / 2) * 8).toString()
}

@BindingAdapter("gamePointsRemaining")
fun TextView.setGamePointsDiff(crtPlayer: CurrentPlayer) {
    text = abs(crtPlayer.getFirst().framePoints - crtPlayer.getSecond().framePoints).toString()
}

// Game Actions
@BindingAdapter("setMissSafeFoulEnabled")
fun TextView.setMissSafeFoulEnabled(size: Int) {
    isEnabled = when (size) {
        in (0..1) -> false
        else -> true
    }
}

@BindingAdapter("undoEnabled")
fun TextView.setUndoEnabled(size: Int) {
    isEnabled = when (size) {
        0 -> false
        else -> true
    }
}

@BindingAdapter("addRedEnabled")
fun TextView.setAddRedEnabled(size: Int) {
    isEnabled = when (size) {
        in (10..36).filter { it % 2 == 0 } -> true
        else -> false
    }
}

@BindingAdapter("endFrameEnabled")
fun TextView.setEndFrameEnabled(crtPlayer: CurrentPlayer) {
    isEnabled = crtPlayer.getFirst().framePoints != crtPlayer.getSecond().framePoints
}

@BindingAdapter("endMatchEnabled")
fun TextView.setEndMatchEnabled(crtPlayer: CurrentPlayer) {
    isEnabled = (crtPlayer.getFirst().framePoints != crtPlayer.getSecond().framePoints)
            || (crtPlayer.getFirst().matchPoints != crtPlayer.getSecond().matchPoints)
}

// Game Foul Dialog
@BindingAdapter("dialogSetRedEnabled")
fun TextView.dialogSetRedEnabled(size: Int) {
    isEnabled = when (size) {
        in 0..8 -> false
        else -> true
    }
}

@BindingAdapter("dialogFreeBallEnabledAction")
fun TextView.setDialogFreeballEnabled(potAction: PotAction?) {
    isEnabled = when (potAction) {
        PotAction.Switch -> true
        else -> false
    }
}

@BindingAdapter("dialogCannotForceDiff", "dialogCannotForceRemaining")
fun TextView.setDialogForceContinueEnabled(crtPlayer: CurrentPlayer, size: Int) {
    val diff = abs(crtPlayer.getFirst().framePoints - crtPlayer.getSecond().framePoints)
    val remaining =
        if (size <= 7) (-(8 - size) * (8 - size) - (8 - size) + 56) / 2
        else 27 + ((size - 7) / 2) * 8
    isEnabled = (remaining - diff) >= 0
}

// Game Gen Dialog
@BindingAdapter("dialogGameGenQuestion")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.CANCEL_MATCH -> "Are you sure you want to cancel the current match? You will lose all match progress"
        MatchAction.END_FRAME -> "Are you sure you want to end this frame?"
        MatchAction.END_MATCH -> "Are you sure you want to end this match?"
        MatchAction.FRAME_ENDED -> "This frame will end. Would you like to proceed?"
        MatchAction.MATCH_ENDED -> "This match will end. Would you like to proceed?"
    }
}