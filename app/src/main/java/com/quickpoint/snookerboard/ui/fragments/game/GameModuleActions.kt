package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.DomainBall.RED
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.BallAdapterType

@Composable
fun GameModuleActions(gameVm: GameViewModel, ballsList: List<DomainBall>) {
    val domainFrame by gameVm.frameState.collectAsState()
    var isLongSelected by remember { mutableStateOf(Toggle.LongShot.isEnabled) }
    var isRestSelected by remember { mutableStateOf(Toggle.RestShot.isEnabled) }

    LaunchedEffect(true) {
        gameVm.eventSettingsUpdated.collect {
            isLongSelected = Toggle.LongShot.isEnabled
            isRestSelected = Toggle.RestShot.isEnabled
        }
    }

    LaunchedEffect(domainFrame) {
        isLongSelected = Toggle.LongShot.isEnabled
        isRestSelected = Toggle.RestShot.isEnabled
    }

    BoxWithConstraints(Modifier.background(BrownDark)) {
        val ballSize = 48.dp
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            ActionButtonsContainer(Modifier.height(ballSize + MaterialTheme.spacing.medium)) {
                ActionButtonsIcons(gameVm, isLongSelected, isRestSelected)
            }
            ActionButtonsContainer(Modifier.height(ballSize + MaterialTheme.spacing.medium)) {
                ActionButtonsBalls(ballsList, ballSize) { potType, domainBall ->
                    gameVm.assignPot(potType, domainBall)
                }
//                VerticalDivider(spacing = 8.dp)
                ActionButtonsBallsExtra(ballsList, ballSize, gameVm.isRemoveColorAvailable()) { potType, domainBall ->
                    gameVm.assignPot(potType, domainBall)
                }
            }
//            HorizontalDivider()
        }
    }
}

@Composable
fun ActionButtonsContainer(
    modifier: Modifier = Modifier,
    text: String = "",
    showDivider: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable RowScope.() -> Unit,
) = Column {
//    if (showDivider) HorizontalDivider()
    if (text != "") {
        Spacer(Modifier.height(16.dp))
        TextSubtitle(text)
        Spacer(Modifier.height(8.dp))
    }
    StandardRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) { content() }
}

@Composable
fun RowScope.ActionButtonsBalls(
    ballsList: List<DomainBall>,
    ballSize: Dp,
    ballAdapterType: BallAdapterType = BallAdapterType.MATCH,
    selectionPosition: Long = -1,
    onClick: (PotType, DomainBall) -> Unit = { _: PotType, _: DomainBall -> },
) = StandardLazyRow(
    Modifier.weight(1f),
    lazyItems = if (ballAdapterType == BallAdapterType.MATCH) ballsList.bindBallOptions() else ballsList.bindFoulBalls(),
    key = { profile -> profile.ballId }
) { profile ->
    BallView(
        modifier = Modifier.size(ballSize),
        profile,
        ballAdapterType,
        isBallSelected = selectionPosition == profile.ballId,
        text = if (ballAdapterType == BallAdapterType.MATCH && profile is RED) ballsList.redsRemaining().toString() else ""
    ) { onClick(TYPE_HIT, profile) }
}

@Composable
fun ActionButtonsBallsExtra(
    ballsList: List<DomainBall>,
    ballSize: Dp,
    isRemoveColorAvailable: Boolean = false,
    onClick: (PotType, DomainBall) -> Unit = { _: PotType, _: DomainBall -> },
) = StandardRow {
    BallView(modifier = Modifier.size(ballSize), NOBALL(), BallAdapterType.MATCH) { onClick(TYPE_MISS, NOBALL()) }
    if (ballsList.isAddRedAvailable() || isRemoveColorAvailable) {
        BallView(
            modifier = Modifier.size(ballSize),
            ball = ballsList.last(),
            ballAdapterType = BallAdapterType.MATCH,
            text = stringResource(if (ballsList.isAddRedAvailable()) R.string.ball_add_one else R.string.ball_remove_one)
        ) { onClick(if (ballsList.isAddRedAvailable()) TYPE_ADDRED else TYPE_REMOVE_COLOR, NOBALL()) }
    }
}

@Composable
fun ActionButtonsIcons(gameVm: GameViewModel, isLongSelected: Boolean, isRestSelected: Boolean) {
    IconButton(
        text = stringResource(R.string.l_game_actions_btn_foul),
        painter = painterResource(R.drawable.ic_action_foul)
    ) { gameVm.assignPot(TYPE_FOUL_ATTEMPT) }
    IconButton(
        text = stringResource(R.string.l_game_actions_btn_safe_success),
        painter = painterResource(R.drawable.ic_action_safe_success),
    ) { gameVm.assignPot(TYPE_SAFE) }
    IconButton(
        text = stringResource(R.string.l_game_actions_btn_safe_miss),
        painter = painterResource(R.drawable.ic_action_safe_miss),
    ) { gameVm.assignPot(TYPE_SAFE_MISS) }
    IconButton(
        text = stringResource(R.string.l_game_actions_btn_snooker),
        painter = painterResource(R.drawable.ic_action_snooker),
    ) { gameVm.assignPot(TYPE_SNOOKER) }

//    VerticalDivider(spacing = 8.dp)
    if (Toggle.AdvancedStatistics.isEnabled)  IconButton(
        text = stringResource(R.string.l_game_actions_btn_long),
        painter = painterResource(R.drawable.ic_action_shot_type_long),
        isSelected = isLongSelected
    ) {
        Toggle.LongShot.toggleEnabled()
        gameVm.onEventSettingsUpdated()
    }
    if (Toggle.AdvancedStatistics.isEnabled) IconButton(
        text = stringResource(R.string.l_game_actions_btn_rest),
        painter = painterResource(R.drawable.ic_action_shot_type_rest),
        isSelected = isRestSelected
    ) {
        Toggle.RestShot.toggleEnabled()
        gameVm.onEventSettingsUpdated()
    }
}