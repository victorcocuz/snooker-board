package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.StandardRow
import com.quickpoint.snookerboard.domain.DomainScore
import java.text.DecimalFormat

@Composable
fun GameModuleStatistics(score: List<DomainScore>) {
    if (score.size == 2) {
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_break_label),
            "${score[0].highestBreak}",
            "${score[1].highestBreak}")
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_success_percentage_label),
            setPercentage(score[0].successShots, score[0].missedShots),
            setPercentage(score[1].successShots, score[1].missedShots)
        )
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_safety_percentage_label),
            setPercentage(score[0].safetySuccessShots, score[0].safetyMissedShots),
            setPercentage(score[1].safetySuccessShots, score[1].safetyMissedShots)
        )
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_snookers_label),
            "${score[0].snookers}",
            "${score[1].snookers}")
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_fouls_label),
            "${score[0].fouls}",
            "${score[1].fouls}")
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_long_shots_label),
            setPercentage(score[0].longShotsSuccess, score[0].longShotsMissed),
            setPercentage(score[1].longShotsSuccess, score[1].longShotsMissed)
        )
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_rest_shots_label),
            setPercentage(score[0].restShotsSuccess, score[0].restShotsMissed),
            setPercentage(score[1].restShotsSuccess, score[1].restShotsMissed)
        )
        SingleStatisticContainer(
            stringResource(R.string.l_game_score_tv_points_with_no_return_label),
            "${score[0].pointsWithoutReturn}",
            "${score[1].pointsWithoutReturn}"
        )
    }
}

@Composable
fun SingleStatisticContainer(type: String, firstValue: String, secondValue: String) = StandardRow {
    StatisticsText(firstValue, 1f)
    StatisticsText(type, 2f)
    StatisticsText(secondValue, 1f)
}

@Composable
fun RowScope.StatisticsText(text: String, weight: Float) = Text(
    modifier = Modifier.weight(weight), textAlign = TextAlign.Center, text = text
)

fun setPercentage(scoreA: Int, scoreB: Int): String {
    val df = DecimalFormat("##%")
    return when ((scoreA + scoreB)) {
        in (1..10000) -> df.format((scoreA.toDouble() / (scoreA.toDouble() + scoreB.toDouble())))
        -2 -> "%"
        else -> "N/A"
    }
}