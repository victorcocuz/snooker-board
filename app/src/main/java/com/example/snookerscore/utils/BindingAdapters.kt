package com.example.snookerscore.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.snookerscore.R
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.Balls
import com.example.snookerscore.fragments.game.CurrentPlayer
import com.example.snookerscore.fragments.game.PotAction

// Ball Item View
@BindingAdapter("ballValue")
fun TextView.setPointsValue(item: Ball?) {
    item?.let {
        text = item.points.toString()
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

// Game fragment and dialog
@BindingAdapter("crtPlayerA")
fun TextView.setCurrentPlayerA(crtPlayer: CurrentPlayer) {
    setBackgroundColor(
        when (crtPlayer) {
            crtPlayer.getFirstPlayer() -> ContextCompat.getColor(context, R.color.design_default_color_primary)
            else -> 0x00000000
        }
    )
}

@BindingAdapter("crtPlayerB")
fun TextView.setCurrentPlayerB(crtPlayer: CurrentPlayer) {
    setBackgroundColor(
        when (crtPlayer) {
            crtPlayer.getFirstPlayer() -> 0x00000000
            else -> ContextCompat.getColor(context, R.color.design_default_color_primary)
        }
    )
}


// Actions
@BindingAdapter("undoEnabled")
fun TextView.setUndoEnabled(size: Int) {
    isEnabled = when (size) {
        37 -> false
        else -> true
    }
}

@BindingAdapter("addRedEnabled")
fun TextView.setAddRedEnabled(size: Int) {
    isEnabled = when (size) {
        in arrayOf(10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36) -> true
        else -> false
    }
}


// Dialog
@BindingAdapter("dialogSetRedEnabled")
fun TextView.dialogSetRedEnabled(size: Int) {
    isEnabled = when (size) {
        in 0..8 -> false
        else -> true
    }
}

@BindingAdapter("dialogFreeBallEnabledSize", "dialogFreeBallEnabledAction")
fun TextView.setDialogFreeballEnabled(size: Int, potAction: PotAction?) {
    isEnabled = when (potAction) {
        PotAction.Continue -> false
        else -> when (size) {
            in 0..8 -> false
            else -> true
        }
    }
}

@BindingAdapter("dialogCannotForceDiff", "dialogCannotForceRemaining")
fun TextView.setDialogForceContinueEnabled(diff: Int, remaining: Int) {
    isEnabled = (remaining - diff) > 0
}