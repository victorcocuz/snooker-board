package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.DomainScore
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownMedium
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.setPercentage

@Composable
fun GameModuleStatistics(score: List<DomainScore>) {
    if (score.size == 2) {
        Column(
            Modifier
                .clip(RoundedCornerShape(MaterialTheme.spacing.extraSmall))
                .background(BrownMedium)
                .padding(MaterialTheme.spacing.small)) {
            SingleStatisticContainer(
                stringResource(R.string.l_game_score_tv_break_label),
                "${score[0].highestBreak}",
                "${score[1].highestBreak}"
            )
            SingleStatisticContainer(
                stringResource(R.string.l_game_score_tv_fouls_label),
                "${score[0].fouls}",
                "${score[1].fouls}"
            )
            SingleStatisticContainer(
                stringResource(R.string.l_game_score_tv_snookers_label),
                "${score[0].snookers}",
                "${score[1].snookers}"
            )
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
}

@Composable
fun SingleStatisticContainer(type: String, firstValue: String, secondValue: String) = StandardRow(Modifier.fillMaxWidth()) {
    TextSubtitle(firstValue, textAlign = TextAlign.Center, color = Beige)
    TextSubtitle(type, Modifier.weight(1f), textAlign = TextAlign.Center, color = Beige)
    TextSubtitle(secondValue, textAlign = TextAlign.Center, color = Beige)
}