package com.quickpoint.snookerboard.fragments.rules

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.compose.ui.styles.*
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.domain.objects.MatchSettings.*
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.fragments.gamedialogs.FragmentDialogGeneric
import com.quickpoint.snookerboard.utils.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentRules(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val focusManager = LocalFocusManager.current
    val rulesVm: RulesViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val dialogVm: DialogViewModel = viewModel(factory = GenericViewModelFactory())
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        mainVm.turnOffSplashScreen()
        rulesVm.eventSharedFlow.collect { event ->
            when (event) {
                is ScreenEvents.SnookerEvent -> {
                    mainVm.onEmit(event.action)
                }

                else -> {} // Not Implemented
            }
        }
    }

    FragmentContent(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }) {
        Row {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.smallMedium)
            ) {
                TextParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_a_label))
                AppTextFieldHoist(
                    rulesVm = rulesVm,
                    key = K_PLAYER01_FIRST_NAME,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                )
                AppTextFieldHoist(
                    rulesVm = rulesVm,
                    key = K_PLAYER01_LAST_NAME,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = MaterialTheme.spacing.smallMedium)
            ) {
                TextParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_b_label))
                AppTextFieldHoist(
                    rulesVm = rulesVm,
                    key = K_PLAYER02_FIRST_NAME,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                )
                AppTextFieldHoist(
                    rulesVm = rulesVm,
                    key = K_PLAYER02_LAST_NAME,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                )
            }
        }
        TextParagraphSubTitle(stringResource(R.string.l_rules_main_hint_name_last))
        NumberPickerHoist(rulesVm = rulesVm)
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_main_tv_breaks_first_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 0)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 2)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 1)
            })
        if (Toggle.AdvancedRules.isEnabled) ToggleAdvancedRulesColumn(rulesVm, dialogVm)
        Button(
            shape = RoundedCornerShape(50.dp),
            onClick = {
                rulesVm.startMatchQuery()
            },
        ) {
            Text(text = "Start Match")
        }
    }
}

@Composable
fun ToggleAdvancedRulesColumn(rulesVm: RulesViewModel, dialogVm: DialogViewModel) =
    Column {
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_reds_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 6)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 10)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 15)
            })
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_foul_modifier_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -3)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -2)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -1)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = 0)
            },
            contentIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_temp_info),
                    contentDescription = null
                )
            },
            onIconClick = { dialogVm.onOpenDialog() }
        )
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_handicap_frame_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = -10)
                RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = 10)
            })
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_handicap_match_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = -1)
                RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = 1)
            })
        if (dialogVm.isGenericDialogShown) {
            FragmentDialogGeneric(
                matchActions = listOf(MatchAction.IGNORE, MatchAction.IGNORE, MatchAction.INFO_FOUL_DIALOG),
                onDismiss = { dialogVm.onDismissDialog() },
                onConfirm = { dialogVm.onEventDialogAction(it) }
            )
        }
    }
