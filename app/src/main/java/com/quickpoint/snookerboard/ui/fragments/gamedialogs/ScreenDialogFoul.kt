package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.BallAdapterType
import com.quickpoint.snookerboard.core.utils.MatchAction.FOUL_DIALOG
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_FREEBALL
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_LONG_SHOT
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_REST_SHOT
import com.quickpoint.snookerboard.domain.models.DomainBall
import com.quickpoint.snookerboard.domain.models.DomainFrame
import com.quickpoint.snookerboard.domain.models.PotAction
import com.quickpoint.snookerboard.domain.models.maxRemoveReds
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.getGenericDialogTitleText

@Composable
fun DialogFoul(gameVm: GameViewModel, dialogVm: DialogViewModel, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (dialogVm.isFoulDialogShown) {
        val frame by gameVm.frameState.collectAsState()
        val actionClicked by dialogVm.actionClicked.collectAsState()
        val dialogReds by dialogVm.eventDialogReds.collectAsState()
        val isCancelable = true

        GenericDialog(isCancelable = isCancelable, onDismissRequest = onDismiss) {
            TextSubtitle(getGenericDialogTitleText(FOUL_DIALOG, FOUL_DIALOG))
            FoulDialogActions(dialogVm, gameVm.ballStack)
            FoulDialogPotAction(dialogVm, frame, actionClicked)
            FoulDialogRedsPottedSlider(dialogReds, frame.ballStack.maxRemoveReds().toFloat(), frame.ballStack.maxRemoveReds() > 0) {
                dialogVm.onDialogReds(it)
            }
            FoulDialogOtherActions(gameVm, frame, actionClicked)
            ContainerRow {
                ButtonStandard(text = stringResource(R.string.f_dialog_foul_btn_cancel)) { onDismiss() }
                ButtonStandard(text = stringResource(R.string.f_dialog_foul_btn_submit)) { onConfirm() }
            }
        }
    }
}

@Composable
fun FoulDialogActions(dialogVm: DialogViewModel, balls: List<DomainBall>) = BoxWithConstraints {
    var selectionPosition by remember { mutableStateOf(-1L) }
    val ballSize = maxWidth / 8

    ContainerRow(title = stringResource(R.string.f_dialog_foul_tv_balls_label)) {
        GameButtonsBalls(balls, ballSize, BallAdapterType.FOUL, selectionPosition) { _, domainBall ->
            selectionPosition = domainBall.ballId
            dialogVm.onBallClicked(domainBall)
        }
    }
}

@Composable
fun FoulDialogPotAction(
    dialogVm: DialogViewModel,
    domainFrame: DomainFrame,
    actionClicked: PotAction,
) = ContainerRow(title = stringResource(R.string.f_dialog_foul_tv_action_label)) {
    ButtonStandard(
        Modifier.weight(1f),
        text = stringResource(R.string.f_dialog_foul_btn_continue),
        height = 56.dp,
        isSelected = actionClicked == PotAction.SWITCH
    ) { dialogVm.onActionClicked(PotAction.SWITCH) }
    Spacer(Modifier.width(8.dp))
    ButtonStandard(
        Modifier.weight(1f),
        text = stringResource(R.string.f_dialog_foul_btn_force_continue),
        height = 56.dp,
        isSelected = actionClicked == PotAction.CONTINUE,
        isEnabled = domainFrame.isFoulAndAMiss()
    ) { dialogVm.onActionClicked(PotAction.CONTINUE) }
    Spacer(Modifier.width(8.dp))
    ButtonStandard(
        Modifier.weight(1f),
        text = stringResource(R.string.f_dialog_foul_btn_force_retake),
        height = 56.dp,
        isSelected = actionClicked == PotAction.RETAKE,
        isEnabled = domainFrame.isFoulAndAMiss()
    ) { dialogVm.onActionClicked(PotAction.RETAKE) }
}

@Composable
fun FoulDialogRedsPottedSlider(
    dialogReds: Int,
    rangeTop: Float,
    show: Boolean = false,
    onValueChange: (Int) -> Unit,
) {
    if (show) ContainerColumn(title = stringResource(R.string.f_dialog_foul_tv_reds_label)) {
        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            Slider(context).apply {
                stepSize = 1f
                value = 0f
                valueFrom = 0f
                valueTo = rangeTop
                labelBehavior = LabelFormatter.LABEL_GONE
                addOnChangeListener { _, value, _ -> onValueChange(value.toInt()) }
            }
        }, update = { slider -> slider.value = dialogReds.toFloat() })

        StandardRow(
            Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextSubtitle(stringResource(R.string._0))
            TextSubtitle(stringResource(R.string._1))
            if (rangeTop > 1) TextSubtitle(stringResource(R.string._2))
            if (rangeTop > 2) TextSubtitle(stringResource(R.string._3))
        }
    }
}

@Composable
fun FoulDialogOtherActions(
    gameVm: GameViewModel,
    domainFrame: DomainFrame,
    actionClicked: PotAction,
) = ContainerRow(
    Modifier.fillMaxWidth(),
    title = stringResource(R.string.f_dialog_foul_tv_shot_type_label),
) {

    val isLongActive by gameVm.dataStoreRepository.toggleLongShot.collectAsState(false)
    val isRestActive by gameVm.dataStoreRepository.toggleRestShot.collectAsState(false)
    val isFreeballActive by gameVm.dataStoreRepository.toggleFreeball.collectAsState(false)
    val isAdvancesStatisticsActive by gameVm.dataStoreRepository.toggleAdvancedStatistics.collectAsState(false)

    val isFreeBallEnabled = actionClicked == PotAction.SWITCH && domainFrame.ballStack.size > 2
    if (!isFreeBallEnabled) gameVm.savePref(K_BOOL_TOGGLE_FREEBALL, false)

    IconButton(
        text = stringResource(R.string.l_game_actions_btn_free_ball),
        painter = painterResource(R.drawable.ic_action_shot_type_free),
        isSelected = isFreeballActive,
        isEnabled = isFreeBallEnabled
    ) { gameVm.savePref(K_BOOL_TOGGLE_FREEBALL,!isFreeballActive) }
    if (isAdvancesStatisticsActive) IconButton(
        text = stringResource(R.string.l_game_actions_btn_long),
        painter = painterResource(R.drawable.ic_action_shot_type_long),
        isSelected = isLongActive
    ) { gameVm.savePref(K_BOOL_TOGGLE_LONG_SHOT, !isLongActive) }
    if (isAdvancesStatisticsActive) IconButton(
        text = stringResource(R.string.l_game_actions_btn_rest),
        painter = painterResource(R.drawable.ic_action_shot_type_rest),
        isSelected = isRestActive
    ) { gameVm.savePref(K_BOOL_TOGGLE_REST_SHOT, !isRestActive) }
}