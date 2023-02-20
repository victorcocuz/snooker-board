package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
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
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.BallAdapterType
import timber.log.Timber

@Composable
fun GameModuleActions(gameVm: GameViewModel, balls: List<DomainBall>) {
    BoxWithConstraints(Modifier.background(BrownDark)) {
        val ballSize = 48.dp
        Timber.e("ballSize $ballSize")
        Column(horizontalAlignment = Alignment.End) {
            ActionButtonsContainer(Modifier.height(ballSize + MaterialTheme.spacing.medium)) {
                ActionButtonsBalls(
                    balls,
                    BallAdapterType.MATCH,
                    ballSize,
                    onClick = { domainBall -> gameVm.assignPot(PotType.TYPE_HIT, domainBall) },
                    onExtraRedClick = {gameVm.assignPot(PotType.TYPE_ADDRED)},
                    onMissClick = { gameVm.assignPot(PotType.TYPE_MISS) })
            }
            ActionButtonsContainer { ActionButtonsSecondary(gameVm) }
        }
    }
}

@Composable
fun ActionButtonsContainer(
    modifier: Modifier = Modifier,
    text: String = "",
    showDivider: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.End,
    content: @Composable RowScope.() -> Unit,
) = Column {
    if (showDivider) HorizontalDivider()
    if (text != "") {
        Spacer(Modifier.height(16.dp))
        TextSubtitle(text)
        Spacer(Modifier.height(8.dp))
    }
    StandardRow(
        modifier,
        horizontalArrangement = horizontalArrangement
    ) { content() }
}

@Composable
fun RowScope.ActionButtonsSecondary(gameVm: GameViewModel) {
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_foul), 0.21f) { gameVm.assignPot(PotType.TYPE_FOUL_ATTEMPT) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe), 0.21f) { gameVm.assignPot(PotType.TYPE_SAFE) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe_miss), 0.3f) { gameVm.assignPot(PotType.TYPE_SAFE_MISS) }
    ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_snooker), 0.28f) { gameVm.assignPot(PotType.TYPE_SNOOKER) }
}

@Composable
fun RowScope.ActionButtonsBalls(
    ballList: List<DomainBall>,
    ballAdapterType: BallAdapterType,
    ballSize: Dp,
    onClick: (DomainBall) -> Unit,
    onExtraRedClick: () -> Unit = {},
    onMissClick: () -> Unit = {},
    selectionPosition: Long = -1,
) {
    LazyRow(
        Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = ballList.bindBallOptions(),
            key = { profile -> profile.ballId }) { profile ->
            Box(contentAlignment = Alignment.Center) {
                BallView(
                    modifier = Modifier.size(ballSize),
                    profile,
                    ballAdapterType,
                    onClick = { onClick(profile) },
                    isBallSelected = selectionPosition == profile.ballId
                )
                if (profile is DomainBall.RED) TextBallInfo(ballList.redsRemaining().toString())
            }
        }
    }
    if (ballAdapterType == BallAdapterType.MATCH) {
        VerticalDivider(spacing = 8.dp)
        if(ballList.isAddRedAvailable()) Box(contentAlignment = Alignment.Center) {
            BallView(
                modifier = Modifier.size(ballSize),
                DomainBall.RED(),
                ballAdapterType,
                onClick = { onExtraRedClick() }
            )
            TextBallInfo("+1")
        }
        BallView(
            modifier = Modifier.size(ballSize),
            DomainBall.NOBALL(),
            ballAdapterType,
            onClick = { onMissClick() }
        )
    }
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
    weight: Float = 1f,
    height: Dp = 40.dp,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    onAction: () -> Unit,
) {
    ButtonStandard(
        Modifier.weight(weight),
        text = text,
        height = height,
        onClick = onAction,
        isSelected = isSelected,
        isEnabled = isEnabled
    )
}