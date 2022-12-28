package com.quickpoint.snookerboard.utils

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.quickpoint.snookerboard.R
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

@BindingAdapter("matchPointsPlayerA", "matchPointsPlayerB")
fun TextView.setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) {
    text = when (matchPointsPlayerA) {
        -1 -> context.getString(R.string.l_summary_tv_header_score)
        else -> context.getString(R.string.f_summary_game_match_score, matchPointsPlayerA, matchPointsPlayerB)
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

@BindingAdapter("setStatsTableBackground")
fun LinearLayout.setStatsTableBackground(type: Int) {
    setBackgroundColor(ContextCompat.getColor(context, when (type) {
        0 -> R.color.green
        1 -> R.color.transparent
        else -> R.color.beige
    }))
}

@BindingAdapter("setStatsTableStyle")
fun TextView.setStatsTableStyle(type: Int) {
    setTextColor(ContextCompat.getColor(context, when (type) {
        0 -> R.color.white
        else -> R.color.black
    }))
}