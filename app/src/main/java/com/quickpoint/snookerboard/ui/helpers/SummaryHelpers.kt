package com.quickpoint.snookerboard.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.StatisticsType
import com.quickpoint.snookerboard.core.utils.StatisticsType.*
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.GreenDark
import com.quickpoint.snookerboard.ui.theme.GreenDarker
import java.text.DecimalFormat

@Composable
fun setPercentage(scoreA: Int, scoreB: Int): String {
    val df = DecimalFormat("##%")
    return when ((scoreA + scoreB)) {
        in (1..10000) -> df.format((scoreA.toDouble() / (scoreA.toDouble() + scoreB.toDouble())))
        -2 -> stringResource(R.string.l_summary_tv_header_percentage)
        else -> "N/A"
    }
}

@Composable
fun setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) = when (matchPointsPlayerA) {
    -1 -> stringResource(R.string.l_summary_tv_header_score)
    else -> stringResource(R.string.f_summary_game_match_score, matchPointsPlayerA, matchPointsPlayerB)
}

@Composable
fun setGameStatsValue(type: StatisticsType, value: Int) = when (value) {
        -1 -> when (type) {
            HIGHEST_BREAK -> stringResource(R.string.l_summary_tv_header_break)
            FRAME_ID -> stringResource(R.string.l_summary_tv_header_frame_id)
            FRAME_POINTS -> stringResource(R.string.l_summary_tv_header_points)
        }
        -2 -> stringResource(R.string.l_summary_tv_footer_total)
        else -> value.toString()
    }

@Composable
fun setStatsTableBackground(index: Int, isLabel: Boolean) = when {
    isLabel -> Beige
    index % 2 == 0 -> GreenDarker
    else -> GreenDark
}