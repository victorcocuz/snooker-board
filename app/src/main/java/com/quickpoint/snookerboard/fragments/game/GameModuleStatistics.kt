package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.StandardRow

@Composable
fun GameModuleStatistics(gameVm: GameViewModel) {
    StatisticsLine(stringResource(R.string.l_game_score_tv_break_label), "50", "20")
    StatisticsLine(stringResource(R.string.l_game_score_tv_success_percentage_label), "55%", "30%")
    StatisticsLine(stringResource(R.string.l_game_score_tv_safety_percentage_label), "50%", "20%")
    StatisticsLine(stringResource(R.string.l_game_score_tv_snookers_label), "3", "5")
    StatisticsLine(stringResource(R.string.l_game_score_tv_fouls_label), "4", "2")
    StatisticsLine(stringResource(R.string.l_game_score_tv_long_shots_label), "2", "4")
    StatisticsLine(stringResource(R.string.l_game_score_tv_rest_shots_label), "1", "5")
    StatisticsLine(stringResource(R.string.l_game_score_tv_points_with_no_return_label), "134", "0")
}

@Composable
fun StatisticsLine(type: String, firstValue: String, secondValue: String) = StandardRow {
    StatisticsText(firstValue, 1f)
    StatisticsText(type, 2f)
    StatisticsText(secondValue, 1f)
}

@Composable
fun RowScope.StatisticsText(text: String, weight: Float) = Text(
    modifier = Modifier.weight(weight),
    textAlign = TextAlign.Center,
    text = text
)