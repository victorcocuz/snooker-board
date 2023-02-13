package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.getNextBallOptions
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.setBallBackground
import timber.log.Timber

@Composable
fun GameModuleActions(gameVm: GameViewModel, frame: DomainFrame, isLongSelected: Boolean, isRestSelected: Boolean) {
    BoxWithConstraints {
        val ballSize = maxWidth / 8
        Column {
        ActionButtonsContainer { ActionButtonsSecondary(gameVm) }
        ActionButtonsContainer(Modifier.height(ballSize + MaterialTheme.spacing.medium)) {
            ActionButtonsBalls(gameVm, frame.ballStack.getNextBallOptions(), ballSize)
        }
        ActionButtonsContainer { ActionButtonsToggles(gameVm, isLongSelected, isRestSelected) }
        }
    }
}

@Composable
fun ActionButtonsContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    HorizontalDivider()
    StandardRow(modifier) { content() }
}

@Composable
fun RowScope.ActionButtonsSecondary(gameVm: GameViewModel) {
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_foul), 0.21f) { gameVm.assignPot(PotType.TYPE_FOUL) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe), 0.21f) { gameVm.assignPot(PotType.TYPE_SAFE) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe_miss), 0.3f) { gameVm.assignPot(PotType.TYPE_SAFE_MISS) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_snooker), 0.28f) { gameVm.assignPot(PotType.TYPE_SNOOKER) }
}

@Composable
fun RowScope.ActionButtonsBalls(
    gameVm: GameViewModel,
    ballList: List<DomainBall>,
    ballSize: Dp
) {
    LazyRow(
        Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Timber.e("ballList ${ballList.getOrNull(0)?.ballType}")
        items(ballList) { ball ->
            Timber.e("fed: ${ball.ballType}")
            BallView(
                modifier = Modifier
                    .size(ballSize)
                    .padding(2.dp),
                onClick = {
                    Timber.e("onClick: ${ball.ballType}")
                    gameVm.assignPot(PotType.TYPE_HIT, ball) },
                onContent = { it.setBallBackground(ball, BallAdapterType.MATCH) })
        }
    }
    VerticalDivider(spacing = 8.dp)
    BallView(
        modifier = Modifier
            .size(ballSize)
            .padding(2.dp),
        onClick = { gameVm.assignPot(PotType.TYPE_MISS) },
        onContent = { it.setBackgroundResource(R.drawable.ic_ball_miss) }
    )
}

@Composable
fun ActionButtonsToggles(gameVm: GameViewModel, isLongSelected: Boolean, isRestSelected: Boolean) {
    ToggleButton(
        text = stringResource(R.string.l_game_actions_btn_long),
        painter = painterResource(R.drawable.ic_temp_shot_type_long),
        isSelected = isLongSelected
    ) {
        Toggle.LongShot.toggleEnabled()
        gameVm.onEventSettingsUpdated()
    }
    ToggleButton(
        text = stringResource(R.string.l_game_actions_btn_rest),
        painter = painterResource(R.drawable.ic_temp_shot_type_rest),
        isSelected = isRestSelected
    ) {
        Toggle.RestShot.toggleEnabled()
        gameVm.onEventSettingsUpdated()
    }
}

@Composable
fun RowScope.ButtonActionHoist(
    text: String,
    weight: Float,
    onAction: () -> Unit,
) {
    ButtonStandard(
        Modifier.weight(weight),
        text = text,
        onClick = { onAction() },
    )
}