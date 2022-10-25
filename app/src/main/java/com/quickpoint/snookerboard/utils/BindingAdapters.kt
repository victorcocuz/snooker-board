package com.quickpoint.snookerboard.utils

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.fragments.game.BallAdapter
import com.quickpoint.snookerboard.fragments.game.BreakAdapter
import com.quickpoint.snookerboard.fragments.postgame.PostGameAdapter
import com.quickpoint.snookerboard.utils.MatchAction.*
import java.text.DecimalFormat

// General
@BindingAdapter("setSelected")
fun TextView.setViewSelected(selected: Boolean) {
    isSelected = selected
    setTextColor(
        ContextCompat.getColor(
            context,
            if (isSelected) R.color.white
            else if (!isEnabled) R.color.white
            else R.color.black
        )
    )
}

@BindingAdapter("setActive")
fun TextView.setEnabledActive(enabled: Boolean) {
    isEnabled = enabled
    setTextColor(ContextCompat.getColor(context, if (!isEnabled) R.color.white else if (isSelected) R.color.white else R.color.black))
}

@BindingAdapter("setVisible")
fun TextView.setVisible(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}

// Game Display
@BindingAdapter("setActivePlayer", "setActivePlayerTag")
fun LinearLayout.setActivePlayer(activePlayer: Boolean, activePlayerTag: PlayerTagType) {
    setBackgroundColor(
        ContextCompat.getColor(
            context,
            if (activePlayer || activePlayerTag == PlayerTagType.STATISTICS) R.color.transparent else R.color.brown
        )
    )
    for (i in 0 until childCount) {
        getChildAt(i).alpha = if (activePlayer || activePlayerTag == PlayerTagType.STATISTICS) 1F else 0.5F
    }
}

@BindingAdapter("setBarWeightScoreFirst", "setBarWeightScoreSecond")
fun LinearLayout.setBarWeight(scoreFirst: Int, scoreSecond: Int) {
    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, scoreFirst.toFloat() / scoreSecond.toFloat())
}

// Statistics Adapters
@BindingAdapter("setFrameScorePercentage")
fun TextView.setFrameScorePercentage(frameScore: DomainPlayerScore?) = frameScore?.apply {
    val df = DecimalFormat("##%")
    text = when ((successShots + missedShots)) {
        in (1..10000) -> df.format((successShots.toDouble() / (successShots.toDouble() + missedShots.toDouble())))
        -1 -> context.getString(R.string.f_statistics_header_percentage)
        else -> "N/A"
    }
}

@BindingAdapter("matchPointsPlayerA", "matchPointsPlayerB")
fun TextView.setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) {
    text = when (matchPointsPlayerA) {
        -1 -> context.getString(R.string.f_statistics_header_score)
        else -> context.getString(R.string.game_match_score, matchPointsPlayerA, matchPointsPlayerB)
    }
}

@BindingAdapter("setGameStatsType", "setGameStatsValue")
fun TextView.setGameStatsValue(type: StatisticsType, value: Int) {
    text = when (value) {
        -1 -> when (type) {
            StatisticsType.FRAME_ID -> context.getString(R.string.f_statistics_header_frame_id)
            StatisticsType.HIGHEST_BREAK -> context.getString(R.string.f_statistics_header_break)
            StatisticsType.FRAME_POINTS -> context.getString(R.string.f_statistics_header_points)
        }
        -2 -> context.getString(R.string.f_statistics_footer_total)
        else -> value.toString()
    }
}

@BindingAdapter("setStatsTableBackground")
fun LinearLayout.setStatsTableBackground(type: Int) {
    setBackgroundColor(
        ContextCompat.getColor(
            context, when (type) {
                0 -> R.color.green
                1 -> R.color.transparent
                else -> R.color.beige
            }
        )
    )
}

@BindingAdapter("setStatsTableStyle")
fun TextView.setStatsTableStyle(type: Int) {
    setTextColor(
        ContextCompat.getColor(
            context, when (type) {
                0 -> R.color.white
                else -> R.color.black
            }
        )
    )
}

// Gen Dialog Adapters
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
        FRAME_ENDED -> "This frame has ended. Would you like to proceed?"
        MATCH_ENDED -> "This match has ended. Would you like to proceed?"
        INFO_FOUL_DIALOG -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
        FRAME_RESPOT_BLACK -> "Looks like you and your opponent are tied at the end of the frame! The black ball will be re-spotted to decide the winner. The player who started the frame will attempt to pot the black first."
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenA")
fun TextView.setDialogGameA(matchAction: MatchAction) {
    text = when (matchAction) {
        MATCH_CANCEL -> "No"
        FRAME_RERACK -> "No"
        FRAME_TO_END -> "No"
        MATCH_TO_END -> "No"
        FRAME_ENDED -> "No"
        MATCH_ENDED -> "No"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenB", "dialogGameGenBScore")
fun TextView.setDialogGameB(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = when {
        frame == null -> View.GONE
        frame.isNoFrameFinished() -> View.GONE
        matchAction == MATCH_ENDED_DISCARD_FRAME -> View.VISIBLE
        else -> View.GONE
    }
    text = when (matchAction) {
        MATCH_ENDED_DISCARD_FRAME -> "Yes & Remove Frame"
        else -> ""
    }
}

@BindingAdapter("dialogGameGenC", "dialogGameGenCActionB", "dialogGameGenCScore")
fun TextView.setDialogGameC(matchAction: MatchAction, matchActionB: MatchAction, frame: DomainFrame?) {
    isEnabled = !(matchActionB == MATCH_ENDED_DISCARD_FRAME && (frame?.isFrameEqual() ?: false)) // This needs adjusting.
    text = when (matchAction) {
        MATCH_CANCEL -> "Yes"
        FRAME_RERACK -> "Yes"
        FRAME_TO_END -> "Yes"
        MATCH_TO_END -> "Yes"
        FRAME_ENDED -> "Yes"
        MATCH_ENDED -> "Yes"
        FRAME_RESPOT_BLACK -> "OK"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameNote", "dialogGameNoteScore")
fun TextView.setDialogGameNote(matchAction: MatchAction, frame: DomainFrame?) {
    visibility = if (matchAction == MATCH_ENDED_DISCARD_FRAME) View.VISIBLE else View.GONE
    text = when {
        frame == null -> ""
        frame.isNoFrameFinished() -> ""
        frame.isFrameWinResultingInMatchTie() -> "NOTE: Keeping the current frame will result in a draw"
        frame.isMatchEqual() -> "NOTE: Discarding the current frame will result in a draw"
        else -> ""
    }
}

// Break Adapters
@BindingAdapter("setBreakPoints", "setBreakPointsPlayer")
fun TextView.setBreakPoints(crtBreak: DomainBreak, player: Int) {
    text = when {
        crtBreak.player == player && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
        crtBreak.player == player && crtBreak.lastPotType() == TYPE_REMOVERED -> "-Red"
        crtBreak.player == player && crtBreak.lastPotType() == TYPE_FOUL -> "Foul"
        crtBreak.player != player && crtBreak.lastPotType() == TYPE_FOUL -> crtBreak.lastBall()?.foul.toString()
        else -> ""
    }
}

@BindingAdapter("setBreakVisibilityBreak", "setBreakVisibilityPlayer")
fun LinearLayout.setBreakVisibility(crtBreak: DomainBreak, player: Int) {
    visibility = when {
        crtBreak.lastPotType() == TYPE_FOUL -> View.VISIBLE
        crtBreak.player == player -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

// Ball Item View
@BindingAdapter("ballValue", "stackSize")
fun TextView.setPointsValue(item: DomainBall?, stackSize: Int?) = stackSize?.let {
    val reds = (stackSize - 7) / 2
    text = if (reds > 0 && item is RED) reds.toString() else ""
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: DomainBall?) {
    item?.let {
        setBackgroundResource(
            when (item) {
                is RED -> R.drawable.ic_ball_red
                is YELLOW -> R.drawable.ic_ball_yellow
                is GREEN -> R.drawable.ic_ball_green
                is BROWN -> R.drawable.ic_ball_brown
                is BLUE -> R.drawable.ic_ball_blue
                is PINK -> R.drawable.ic_ball_pink
                is BLACK -> R.drawable.ic_ball_black
                is FREEBALL -> R.drawable.ic_ball_grey
                is NOBALL -> R.drawable.ic_ball_grey
                else -> R.drawable.ic_ball_white
            }
        )
    }
}

// RV Adapters
@BindingAdapter("bindMatchBallsRv")
fun RecyclerView.bindBallsRv(ballList: MutableList<DomainBall>?) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(
        when (ballList?.lastOrNull()) {
            is COLOR -> listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
            is WHITE -> listOf(NOBALL())
            else -> listOf(ballList?.lastOrNull())
        }
    )
}

@BindingAdapter("bindFoulBalls")
fun RecyclerView.bindFoulBalls(ballStack: MutableList<DomainBall>) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(
        when (ballStack.size) {
            in (2..7) -> ballStack.reversed()
            else -> listOf(WHITE(), RED(), YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
        }
    )
}

@BindingAdapter("bindGameStatsData")
fun RecyclerView.bindGameStatsRv(data: ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>?) {
    val adapter = this.adapter as PostGameAdapter
    adapter.submitList(data)
}

@BindingAdapter("bindBreakData")
fun RecyclerView.bindBreakRv(breaks: MutableList<DomainBreak>?) = breaks?.let {
    val adapter = this.adapter as BreakAdapter
    adapter.submitList(breaks.getDisplayShots())
}

@BindingAdapter("bindPots", "bindPotsPlayer")
fun RecyclerView.bindPotsRv(crtBreak: DomainBreak?, player: Int) {
    val adapter = this.adapter as BallAdapter
    val balls = mutableListOf<DomainBall>()
    crtBreak?.pots?.forEach {
        balls.add(it.ball)
    }
    adapter.submitList(if (crtBreak?.player == player) balls else mutableListOf())
}