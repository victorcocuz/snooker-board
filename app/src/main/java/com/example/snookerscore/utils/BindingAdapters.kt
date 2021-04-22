package com.example.snookerscore.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.R
import com.example.snookerscore.domain.Ball
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.CurrentScore
import com.example.snookerscore.domain.FrameScore
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.fragments.gamestatistics.GameStatsAdapter
import com.example.snookerscore.fragments.rankings.RankingsAdapter
import java.text.DecimalFormat
import kotlin.math.abs

// Ball Item View
@BindingAdapter("ballValue", "stackSize")
fun TextView.setPointsValue(item: Ball, stackSize: Int) {
    val reds = (stackSize - 7) / 2
    text = if (reds > 0 && item == RED) reds.toString() else ""
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: Ball?) {
    item?.let {
        setBackgroundResource(
            when (item) {
                RED -> R.drawable.ball_red
                YELLOW -> R.drawable.ball_yellow
                GREEN -> R.drawable.ball_green
                BROWN -> R.drawable.ball_brown
                BLUE -> R.drawable.ball_blue
                PINK -> R.drawable.ball_pink
                BLACK -> R.drawable.ball_black
                FREEBALL -> R.drawable.ball_grey
                else -> R.drawable.ball_white
            }
        )
    }
}

// Game Display
@BindingAdapter("crtPlayerA")
fun TextView.setCurrentPlayerA(crtPlayer: CurrentScore) {
    setBackgroundColor(
        when (crtPlayer) {
            crtPlayer.getFirst() -> ContextCompat.getColor(context, R.color.design_default_color_primary)
            else -> 0x00000000
        }
    )
}

@BindingAdapter("crtPlayerB")
fun TextView.setCurrentPlayerB(crtPlayer: CurrentScore) {
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

@BindingAdapter("gamePointsDiffSize")
fun TextView.setGamePointsRemaining(size: Int) {
    text = if (size <= 7) ((-(8 - size) * (8 - size) - (8 - size) + 56) / 2).toString()
    else (27 + ((size - 7) / 2) * 8).toString()
}

@BindingAdapter("gamePointsRemaining")
fun TextView.setGamePointsDiff(crtPlayer: CurrentScore) {
    text = abs(crtPlayer.getFirst().framePoints - crtPlayer.getSecond().framePoints).toString()
}

@BindingAdapter("shotSuccess", "shotMiss")
fun TextView.setShotPercentage(success: Double, miss: Double) {
    val df = DecimalFormat("##%")
    text = when (success + miss) {
        in (0.1..10000.0) -> df.format((success / (success + miss)))
        -2.0 -> "%"
        else -> "N/A"
    }
}

@BindingAdapter("matchPointsPlayerA", "matchPointsPlayerB")
fun TextView.setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) {
    text = when (matchPointsPlayerA) {
        -1 -> context.getString(R.string.fragment_statistics_header_score)
        else -> context.getString(R.string.game_match_score, matchPointsPlayerA, matchPointsPlayerB)
    }
}

// Game Statistics Fragment
@BindingAdapter("gameStatsFrameNumber")
fun TextView.bindGameStatsFrameNumber(frameCount: Int) {
    text = when (frameCount) {
        -1 -> "#"
        -2 -> context.getString(R.string.fragment_statistics_footer_total)
        else -> frameCount.toString()
    }
}

@BindingAdapter("gameStatsBreak")
fun TextView.bindGameStatsBreak(highestBreak: Int) {
    text = when(highestBreak) {
        -1 -> context.getString(R.string.fragment_statistics_header_break)
        else -> highestBreak.toString()
    }
}

@BindingAdapter("gameStatsPoints")
fun TextView.bindGameStatsPoints(framePoints: Int) {
    text = when(framePoints) {
        -1 -> context.getString(R.string.fragment_statistics_header_points)
        else -> framePoints.toString()
    }
}

// Game Actions
@BindingAdapter("setMissSafeFoulEnabled")
fun TextView.setMissSafeFoulEnabled(size: Int) {
    isEnabled = when (size) {
        in (0..1) -> false
        else -> true
    }
}

@BindingAdapter("undoAndRerackEnabled")
fun TextView.setUndoAndRerackEnabled(size: Int) {
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
fun TextView.setEndFrameEnabled(isFrameEqual: Boolean) {
    isEnabled = !isFrameEqual
}

@BindingAdapter("endMatchEnabledFrameEqual", "endMatchEnabledMatchEqual")
fun TextView.setEndMatchEnabled(isFrameEqual: Boolean, isMatchEqual: Boolean) {
    isEnabled = !(isFrameEqual && isMatchEqual)
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
        PotAction.SWITCH -> true
        else -> false
    }
}

@BindingAdapter("dialogCannotForceDiff", "dialogCannotForceRemaining")
fun TextView.setDialogForceContinueEnabled(crtPlayer: CurrentScore, size: Int) {
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

// RV Adapters
@BindingAdapter("listRankingsData")
fun bindRankingsRv(recyclerView: RecyclerView, data: List<DomainRanking>?) {
    val adapter = recyclerView.adapter as RankingsAdapter
    adapter.submitList(data)
}


@BindingAdapter("listGameStatsData")
fun bindGameStatsRv(recyclerView: RecyclerView, data: ArrayList<Pair<FrameScore, FrameScore>>?) {
    val adapter = recyclerView.adapter as GameStatsAdapter
    adapter.submitList(data)
}