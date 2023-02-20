package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.base.EventObserver
import com.quickpoint.snookerboard.databinding.FragmentDialogFoulBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.DialogCard
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.components.ToggleButton
import com.quickpoint.snookerboard.ui.fragments.game.ActionButtonsBalls
import com.quickpoint.snookerboard.ui.fragments.game.ActionButtonsContainer
import com.quickpoint.snookerboard.ui.fragments.game.ButtonActionHoist
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*

class FoulDialogFragment : DialogFragment() {
    private val dialogVm: DialogViewModel by activityViewModels()
    private lateinit var gameVm: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutSizeByFactor(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentDialogFoulBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_foul, container, false)
        gameVm = ViewModelProvider(requireParentFragment().childFragmentManager.fragments[0])[GameViewModel::class.java]

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varGameVm = gameVm
            varDialogVm = dialogVm

//            fDialogFoulRvBalls.apply {
//                layoutManager = GridLayoutManager(activity, 4)
//                adapter = BallAdapter(
//                    BallListener { ball -> dialogVm.onBallClicked(ball) },
//                    MutableLiveData(),
//                    BallAdapterType.FOUL
//                )
//            }
        }

        // Observers
        dialogVm.apply {
            eventDialogAction.observe(viewLifecycleOwner, EventObserver { action ->
                when (action) {
                    FOUL_ATTEMPT ->
                        if (foulIsValid()) {
                            repeat(eventDialogReds.value) { gameVm.onEventGameAction(FRAME_REMOVE_RED, true) }
                            gameVm.onEventGameAction(FOUL_CONFIRM, true)
                            if (Toggle.FreeBall.isEnabled) gameVm.onEventGameAction(FRAME_FREE_ACTIVE, true)
                            dismiss()
                        } else toast(getString(R.string.toast_f_dialog_foul_invalid))
                    else -> {
                        dialogVm.onDismissFoulDialog()
                        dismiss()
                    }
                }
            })
        }
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        dialogVm.onDismissFoulDialog()
        super.onCancel(dialog)
    }
}

@Composable
fun DialogFoul(gameVm: GameViewModel, dialogVm: DialogViewModel, mainVm: MainViewModel) {
    if (dialogVm.isFoulDialogShown) {
        FragmentDialogFoul(
            gameVm = gameVm,
            dialogVm = dialogVm,
            mainVm = mainVm,
            onDismiss = { dialogVm.onDismissFoulDialog() },
            onConfirm = { dialogVm.onEventDialogAction(it) }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentDialogFoul(
    gameVm: GameViewModel,
    dialogVm: DialogViewModel,
    mainVm: MainViewModel,
    onDismiss: () -> Unit,
    onConfirm: (MatchAction) -> Unit,
) {
    val domainFrame by gameVm.frameState.collectAsState()
    val actionClicked by dialogVm.actionClicked.collectAsState()
    val dialogReds by dialogVm.eventDialogReds.collectAsState()
    val isCancelable = true
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = isCancelable,
            dismissOnClickOutside = isCancelable
        )
    ) {
        DialogCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {

                var isLongSelected by remember { mutableStateOf(Toggle.LongShot.isEnabled) }
                var isRestSelected by remember { mutableStateOf(Toggle.RestShot.isEnabled) }
                var isFreeBallSelected by remember { mutableStateOf(Toggle.FreeBall.isEnabled) }
                LaunchedEffect(true) {
                    gameVm.eventSettingsUpdated.collect {
                        isLongSelected = Toggle.LongShot.isEnabled
                        isRestSelected = Toggle.RestShot.isEnabled
                        isFreeBallSelected = Toggle.FreeBall.isEnabled
                    }
                }

                TextSubtitle(getGenericDialogTitleText(FOUL_DIALOG, FOUL_DIALOG))
                FoulDialogActions(dialogVm, gameVm.ballStack.bindFoulBalls())
                ActionButtonsContainer(text = stringResource(R.string.f_dialog_foul_tv_action_label)) {
                    ButtonActionHoist(
                        text = stringResource(R.string.f_dialog_foul_btn_continue),
                        height = 56.dp,
                        isSelected = actionClicked == PotAction.SWITCH
                    ) { dialogVm.onActionClicked(PotAction.SWITCH) }
                    Spacer(Modifier.width(8.dp))
                    ButtonActionHoist(
                        text = stringResource(R.string.f_dialog_foul_btn_force_continue),
                        height = 56.dp,
                        isSelected = actionClicked == PotAction.CONTINUE,
                        isEnabled = domainFrame.isFoulAndAMiss()
                    ) { dialogVm.onActionClicked(PotAction.CONTINUE) }
                    Spacer(Modifier.width(8.dp))
                    ButtonActionHoist(
                        text = stringResource(R.string.f_dialog_foul_btn_force_retake),
                        height = 56.dp,
                        isSelected = actionClicked == PotAction.RETAKE,
                        isEnabled = domainFrame.isFoulAndAMiss()
                    ) { dialogVm.onActionClicked(PotAction.RETAKE) }
                }
                if (domainFrame.ballStack.maxRemoveReds() > 0)
                    ActionButtonsContainer(text = stringResource(R.string.f_dialog_foul_tv_reds_label)) {
                        RedsPottedOnFoulSlider(dialogReds, domainFrame.ballStack.maxRemoveReds().toFloat()) { dialogVm.onDialogReds(it) }
                    }
                ActionButtonsContainer(
                    Modifier.fillMaxWidth(),
                    text = stringResource(R.string.f_dialog_foul_tv_shot_type_label),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val isFreeBallEnabled = actionClicked == PotAction.SWITCH && domainFrame.ballStack.size > 2
                    if (!isFreeBallEnabled) {
                        Toggle.FreeBall.setDisabled()
                        gameVm.onEventSettingsUpdated()
                    }
                    ToggleButton(
                        text = stringResource(R.string.l_game_actions_btn_free_ball),
                        painter = painterResource(R.drawable.ic_temp_freeball),
                        isSelected = isFreeBallSelected,
                        isEnabled = isFreeBallEnabled
                    ) {
                        Toggle.FreeBall.toggleEnabled()
                        gameVm.onEventSettingsUpdated()
                    }
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
                ActionButtonsContainer(Modifier.fillMaxWidth(), showDivider = false)
                {
                    ButtonGenericDialogHoist(stringResource(R.string.f_dialog_foul_btn_cancel)) { onDismiss() }
                    ButtonGenericDialogHoist(stringResource(R.string.f_dialog_foul_btn_submit)) {
                        if (dialogVm.foulIsValid()) {
                            repeat(dialogVm.eventDialogReds.value.toInt()) { gameVm.onEventGameAction(FRAME_REMOVE_RED, true) }
                            gameVm.onEventGameAction(FOUL_CONFIRM, true)
                            if (Toggle.FreeBall.isEnabled) gameVm.onEventGameAction(FRAME_FREE_ACTIVE, true)
                        } else mainVm.onEmit(ScreenEvents.SnackEvent(SNACK_INVALID_FOUL))
                    }
                }
            }
        }
    }
}

@Composable
fun RedsPottedOnFoulSlider(
    dialogReds: Int,
    rangeTop: Float,
    onValueChange: (Int) -> Unit,
) = Column {
    AndroidView(modifier = Modifier.fillMaxWidth(),
        factory = {context ->
            Slider(context).apply {
                stepSize = 1f
                value = 0f
                valueFrom = 0f
                valueTo = rangeTop
                labelBehavior = LabelFormatter.LABEL_GONE
                onChangeListener { onValueChange(it) }
            }
        },
    update = {slider ->
        slider.value = dialogReds.toFloat()
    })

    StandardRow(
        Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextSubtitle(stringResource(R.string._0))
        TextSubtitle(stringResource(R.string._1))
        if (rangeTop > 1) TextSubtitle(stringResource(R.string._2))
        if (rangeTop > 2) TextSubtitle(stringResource(R.string._3))
    }
}

@Composable
fun FoulDialogActions(dialogVm: DialogViewModel, balls: List<DomainBall>) {
    var selectionPosition by remember { mutableStateOf(-1L) }

    BoxWithConstraints {
        val ballSize = maxWidth / 8
        Column(horizontalAlignment = Alignment.End) {
            ActionButtonsContainer(
                Modifier.height(ballSize + MaterialTheme.spacing.medium),
                text = stringResource(R.string.f_dialog_foul_tv_balls_label)
            ) {
                ActionButtonsBalls(
                    balls,
                    BallAdapterType.FOUL,
                    ballSize,
                    onClick = { domainBall ->
                        selectionPosition = domainBall.ballId
                        dialogVm.onBallClicked(domainBall)
                    },
                    selectionPosition = selectionPosition
                )
            }
        }
    }
}