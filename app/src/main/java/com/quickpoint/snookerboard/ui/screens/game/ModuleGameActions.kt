package com.quickpoint.snookerboard.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.BallAdapterType
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_LONG_SHOT
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_REST_SHOT
import com.quickpoint.snookerboard.domain.models.*
import com.quickpoint.snookerboard.domain.models.DomainBall.NOBALL
import com.quickpoint.snookerboard.domain.models.PotType.*
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.theme.BrownDark

@Composable
fun ModuleGameActions(gameVm: GameViewModel, ballsList: List<DomainBall>, frameStack: List<DomainBreak>) = ContainerColumn(Modifier.background(BrownDark)) {
    val ballSize = 48.dp

    GameButtonsIcons(gameVm)
    StandardRow {
        GameButtonsBalls(ballsList, ballSize) { potType, domainBall -> gameVm.assignPot(potType, domainBall) }
        GameButtonsBallsExtra(ballsList, ballSize, canRemoveColor(ballsList, frameStack)) { potType, domainBall -> gameVm.assignPot(potType, domainBall) }
    }
}

@Composable
fun GameButtonsIcons(gameVm: GameViewModel) {
    val isLongActive by gameVm.dataStoreRepository.toggleLongShot.collectAsState(false)
    val isRestActive by gameVm.dataStoreRepository.toggleRestShot.collectAsState(false)
    val isAdvancedStatistics by gameVm.dataStoreRepository.toggleAdvancedStatistics.collectAsState(false)

    StandardRow(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
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

        if (isAdvancedStatistics) IconButton(
            text = stringResource(R.string.l_game_actions_btn_long),
            painter = painterResource(R.drawable.ic_action_shot_type_long),
            isSelected = isLongActive
        ) { gameVm.dataStoreRepository.savePref(K_BOOL_TOGGLE_LONG_SHOT, !isLongActive) }
        if (isAdvancedStatistics) IconButton(
            text = stringResource(R.string.l_game_actions_btn_rest),
            painter = painterResource(R.drawable.ic_action_shot_type_rest),
            isSelected = isRestActive
        ) { gameVm.dataStoreRepository.savePref(K_BOOL_TOGGLE_REST_SHOT, !isRestActive) }
    }
}

@Composable
fun GameButtonsBallsExtra(
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

fun canRemoveColor(ballStack: List<DomainBall>, frameStack: List<DomainBreak>) = ballStack.isInColors() && frameStack.lastPotType() == TYPE_FREE
