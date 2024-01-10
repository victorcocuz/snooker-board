package com.quickpoint.snookerboard.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.models.DomainScore
import com.quickpoint.snookerboard.ui.components.ContainerColumn
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.helpers.setPercentage
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownMedium
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun ModuleGameStatistics(
    score: List<DomainScore>, show: Boolean,
) = ContainerColumn(
    Modifier
        .clip(RoundedCornerShape(MaterialTheme.spacing.extraSmall))
        .background(BrownMedium)
        .padding(8.dp)
) {
    if (show) {
        ComponentStatistics(stringResource(R.string.l_game_score_tv_break_label), "${score[0].highestBreak}", "${score[1].highestBreak}")
        ComponentStatistics(stringResource(R.string.l_game_score_tv_fouls_label), "${score[0].fouls}", "${score[1].fouls}")
        ComponentStatistics(stringResource(R.string.l_game_score_tv_snookers_label), "${score[0].snookers}", "${score[1].snookers}")
        ComponentStatistics(
            stringResource(R.string.l_game_score_tv_success_percentage_label),
            setPercentage(score[0].successShots, score[0].missedShots),
            setPercentage(score[1].successShots, score[1].missedShots)
        )
        ComponentStatistics(
            stringResource(R.string.l_game_score_tv_safety_percentage_label),
            setPercentage(score[0].safetySuccessShots, score[0].safetyMissedShots),
            setPercentage(score[1].safetySuccessShots, score[1].safetyMissedShots)
        )
        ComponentStatistics(
            stringResource(R.string.l_game_score_tv_long_shots_label),
            setPercentage(score[0].longShotsSuccess, score[0].longShotsMissed),
            setPercentage(score[1].longShotsSuccess, score[1].longShotsMissed)
        )
        ComponentStatistics(
            stringResource(R.string.l_game_score_tv_rest_shots_label),
            setPercentage(score[0].restShotsSuccess, score[0].restShotsMissed),
            setPercentage(score[1].restShotsSuccess, score[1].restShotsMissed)
        )
        ComponentStatistics(
            stringResource(R.string.l_game_score_tv_points_with_no_return_label),
            "${score[0].pointsWithoutReturn}",
            "${score[1].pointsWithoutReturn}"
        )
    }
}

@Composable
fun ComponentStatistics(type: String, firstValue: String, secondValue: String) = StandardRow(Modifier.fillMaxWidth()) {
    TextSubtitle(firstValue, textAlign = TextAlign.Center, color = Beige)
    TextSubtitle(type, Modifier.weight(1f), textAlign = TextAlign.Center, color = Beige)
    TextSubtitle(secondValue, textAlign = TextAlign.Center, color = Beige)
}