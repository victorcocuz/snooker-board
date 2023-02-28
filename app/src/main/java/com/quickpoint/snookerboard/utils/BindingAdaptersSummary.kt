package com.quickpoint.snookerboard.utils

import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.GreenDark
import com.quickpoint.snookerboard.ui.theme.GreenDarker
import com.quickpoint.snookerboard.utils.StatisticsType.*
import java.text.DecimalFormat

// Statistics Adapters
@BindingAdapter("setPercentageA", "setPercentageB")
fun TextView.setPercentage(scoreA: Int, scoreB: Int) {
    val df = DecimalFormat("##%")
    text = when ((scoreA + scoreB)) {
        in (1..10000) -> df.format((scoreA.toDouble() / (scoreA.toDouble() + scoreB.toDouble())))
        -2 -> context.getString(R.string.l_summary_tv_header_percentage)
        else -> "N/A"
    }
}

@Composable
fun setPercentage(scoreA: Int, scoreB: Int): String {
    val df = DecimalFormat("##%")
    return when ((scoreA + scoreB)) {
        in (1..10000) -> df.format((scoreA.toDouble() / (scoreA.toDouble() + scoreB.toDouble())))
        -2 -> stringResource(R.string.l_summary_tv_header_percentage)
        else -> "N/A"
    }
}

@BindingAdapter("matchPointsPlayerA", "matchPointsPlayerB")
fun TextView.setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) {
    text = when (matchPointsPlayerA) {
        -1 -> context.getString(R.string.l_summary_tv_header_score)
        else -> context.getString(R.string.f_summary_game_match_score, matchPointsPlayerA, matchPointsPlayerB)
    }
}

@Composable
fun setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int): String {
   return when (matchPointsPlayerA) {
        -1 -> stringResource(R.string.l_summary_tv_header_score)
        else -> stringResource(R.string.f_summary_game_match_score, matchPointsPlayerA, matchPointsPlayerB)
    }
}

@BindingAdapter("setGameStatsType", "setGameStatsValue")
fun TextView.setGameStatsValue(type: StatisticsType, value: Int) {
    text = when (value) {
        -1 -> when (type) {
            HIGHEST_BREAK -> context.getString(R.string.l_summary_tv_header_break)
            FRAME_ID -> context.getString(R.string.l_summary_tv_header_frame_id)
            FRAME_POINTS -> context.getString(R.string.l_summary_tv_header_points)
        }
        -2 -> context.getString(R.string.l_summary_tv_footer_total)
        else -> value.toString()
    }
}

@Composable
fun setGameStatsValue(type: StatisticsType, value: Int) =
    when (value) {
        -1 -> when (type) {
            HIGHEST_BREAK -> stringResource(R.string.l_summary_tv_header_break)
            FRAME_ID -> stringResource(R.string.l_summary_tv_header_frame_id)
            FRAME_POINTS -> stringResource(R.string.l_summary_tv_header_points)
        }
        -2 -> stringResource(R.string.l_summary_tv_footer_total)
        else -> value.toString()
    }

@BindingAdapter("setStatsTableBackground")
fun LinearLayout.setStatsTableBackground(type: Int) {
    setBackgroundColor(ContextCompat.getColor(context, when (type) {
        0 -> R.color.green
        1 -> R.color.green_bright
        else -> R.color.beige
    }))
}

@Composable
fun setStatsTableBackground(index: Int, size: Int) = when {
    size == 1 -> Beige
    index in (0..size).filter { x -> x % 2 == 0 } -> GreenDarker
    else -> GreenDark
}

@BindingAdapter("setStatsTableStyle")
fun TextView.setStatsTableStyle(type: Int) {
    setTextColor(ContextCompat.getColor(context, when (type) {
        0 -> R.color.white
        else -> R.color.black
    }))
}