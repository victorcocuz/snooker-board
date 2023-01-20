package com.quickpoint.snookerboard.fragments.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.compose.ui.styles.AppTextField
import com.quickpoint.snookerboard.compose.ui.styles.ButtonStandardHoist
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.NumberPickerHoist
import com.quickpoint.snookerboard.compose.ui.styles.RuleSelectionItem
import com.quickpoint.snookerboard.compose.ui.styles.RulesHandicapLabel
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchSettings.Settings
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

class RulesFragment : Fragment() {
    // Variables
    private val mainVm: MainViewModel by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        if (Settings.matchState == RULES_IDLE) mainVm.transitionToFragment(this, 0) // For returning from navDrawer
        mainVm.matchState.observe(viewLifecycleOwner, EventObserver { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                RULES_IDLE -> {
                    mainVm.transitionToFragment(this, 0)
                }

                GAME_IN_PROGRESS, SUMMARY -> {
                    startPostponedEnterTransition()
                    view?.visibility = GONE
                    navigate(if (matchState == GAME_IN_PROGRESS) RulesFragmentDirections.gameFrag() else RulesFragmentDirections.summaryFrag())
                }

                else -> Timber.e("No implementation for state $matchState at this point")
            }
        })

        // VM Observers
        rulesVm.eventRulesAction.observe(viewLifecycleOwner, EventObserver { action ->
            when (action) { // Start new match
                MatchAction.INFO_FOUL_DIALOG -> navigate(
                    RulesFragmentDirections.genDialogFrag(
                        MatchAction.IGNORE,
                        MatchAction.IGNORE, action
                    )
                )

                else -> Timber.i("Implementation for observed action $action not supported")
            }
        })

        return ComposeView(requireContext()).apply {
            setContent {
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentRules(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val focusManager = LocalFocusManager.current
    val rulesVm: RulesViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        rulesVm.updatePlayerNames(dataStore.readAllNames())
        rulesVm.updateRules(dataStore.readAllRules())
        rulesVm.eventSharedFlow.collect { event ->
            when (event) {
                is ScreenEvents.SnookerEvent -> {
                    mainVm.onEmit(event.action)
                }

                else -> {} // Not Implemented
            }
        }
    }

    FragmentColumn(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }) {
        Row {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.smallMedium)
            ) {
                TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_a_label))
                AppTextField(
                    text = rulesVm.players[USER_PLAYER01_FIRST_NAME_KEY]?.value ?: "",
                    placeholder = stringResource(R.string.l_rules_main_hint_name_first),
                    onChange = { rulesVm.updatePlayerNames(mapOf(USER_PLAYER01_FIRST_NAME_KEY to it)) },
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                AppTextField(
                    text = rulesVm.players[USER_PLAYER01_LAST_NAME_KEY]?.value ?: "",
                    placeholder = stringResource(R.string.l_rules_main_hint_name_last),
                    onChange = { rulesVm.updatePlayerNames(mapOf(USER_PLAYER01_LAST_NAME_KEY to it)) },
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = MaterialTheme.spacing.smallMedium)
            ) {
                TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_b_label))
                AppTextField(
                    text = rulesVm.players[USER_PLAYER02_FIRST_NAME_KEY]?.value ?: "",
                    placeholder = stringResource(R.string.l_rules_main_hint_name_first),
                    onChange = { rulesVm.updatePlayerNames(mapOf(USER_PLAYER02_FIRST_NAME_KEY to it)) },
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                AppTextField(
                    text = rulesVm.players[USER_PLAYER02_LAST_NAME_KEY]?.value ?: "",
                    placeholder = stringResource(R.string.l_rules_main_hint_name_last),
                    onChange = { rulesVm.updatePlayerNames(mapOf(USER_PLAYER02_LAST_NAME_KEY to it)) },
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }
        }
        TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_hint_name_last))
        NumberPickerHoist(rulesVm = rulesVm)
        RuleSelectionItem(stringResource(R.string.l_rules_main_tv_breaks_first_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_tv_player_a_label),
                rulesVm = rulesVm,
                key = KEY_INT_MATCH_STARTING_PLAYER,
                value = 0
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_btn_breaks_random),
                rulesVm = rulesVm,
                key = KEY_INT_MATCH_STARTING_PLAYER,
                value = 2
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_tv_player_a_label),
                rulesVm = rulesVm,
                key = KEY_INT_MATCH_STARTING_PLAYER,
                value = 1
            )
        }
        if (MatchToggleType.ADVANCED_RULES.getToggle().isEnabled) {
            ToggleAdvancedRulesColumn(rulesVm)
        }
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
fun ToggleAdvancedRulesColumn(rulesVm: RulesViewModel) = Column {
    RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_reds_label)) {
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_reds_six),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_AVAILABLE_REDS,
            value = 6,
        )
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_reds_ten),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_AVAILABLE_REDS,
            value = 10
        )
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_reds_fifteen),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_AVAILABLE_REDS,
            value = 15
        )
    }
    RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_foul_modifier_label)) {
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_foul_one),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_FOUL_MODIFIER,
            value = -3,
        )
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_foul_two),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_FOUL_MODIFIER,
            value = -2
        )
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_foul_three),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_FOUL_MODIFIER,
            value = -1
        )
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_foul_four),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_FOUL_MODIFIER,
            value = 0
        )
    }
    RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_handicap_frame_label)) {
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_handicap_frame_less),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_HANDICAP_FRAME,
            value = -10,
        )
        RulesHandicapLabel(rulesVm = rulesVm, key = KEY_INT_MATCH_HANDICAP_FRAME)
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_handicap_frame_more),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_HANDICAP_FRAME,
            value = 10
        )
    }
    RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_handicap_match_label)) {
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_handicap_match_less),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_HANDICAP_MATCH,
            value = -1,
        )
        RulesHandicapLabel(rulesVm = rulesVm, key = KEY_INT_MATCH_HANDICAP_MATCH)
        ButtonStandardHoist(
            text = stringResource(R.string.l_rules_extra_btn_handicap_match_more),
            rulesVm = rulesVm,
            key = KEY_INT_MATCH_HANDICAP_MATCH,
            value = 1
        )
    }
}