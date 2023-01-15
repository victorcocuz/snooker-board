package com.quickpoint.snookerboard.fragments.rules

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.AppTextField
import com.quickpoint.snookerboard.compose.ui.styles.ButtonStandardHoist
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.GenericSurface
import com.quickpoint.snookerboard.compose.ui.styles.RuleSelectionItem
import com.quickpoint.snookerboard.compose.ui.styles.RulesHandicapLabel
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.theme.SnookerBoardTheme
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.databinding.FragmentRulesBinding
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.*
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.GAME_IN_PROGRESS
import com.quickpoint.snookerboard.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.utils.MatchState.SUMMARY
import com.quickpoint.snookerboard.utils.Toggle
import com.quickpoint.snookerboard.utils.hideKeyboard
import com.quickpoint.snookerboard.utils.navigate
import com.quickpoint.snookerboard.utils.snackbar
import com.shawnlin.numberpicker.NumberPicker
import timber.log.Timber

class RulesFragment : Fragment() {
    // Variables
    private val mainVm: MainViewModel by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        if (SETTINGS.matchState == RULES_IDLE) mainVm.transitionToFragment(this, 0) // For returning from navDrawer
        mainVm.matchState.observe(viewLifecycleOwner, EventObserver { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                RULES_IDLE -> {
                    rulesVm.updateRules()
                    rulesVm.updatePlayer()
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

        // Bind view elements
        val binding: FragmentRulesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varRulesVm = rulesVm

            // Bind fragment rules elements
            fRulesLMain.apply {
                varRulesVm = rulesVm
                lRulesMainNpFrameCount.apply { // For displayedValues get an array of odd numbers for the number of frames
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }
            }
            fRulesLExtra.apply {
                varRulesVm = rulesVm
            }
        }

        // VM Observers
        rulesVm.eventRulesAction.observe(viewLifecycleOwner, EventObserver { action ->
            when (action) { // Start new match
                MatchAction.INFO_FOUL_DIALOG -> navigate(
                    RulesFragmentDirections.genDialogFrag(
                        MatchAction.IGNORE,
                        MatchAction.IGNORE, action
                    )
                )

                MatchAction.MATCH_PLAY -> {
                    Timber.i(SETTINGS.getAsText())
                    navigate(RulesFragmentDirections.gameFrag())
                }

                MatchAction.SNACK_NO_PLAYER, MatchAction.SNACK_NO_FIRST, MatchAction.SNACK_HANDICAP_FRAME_LIMIT, MatchAction.SNACK_HANDICAP_MATCH_LIMIT -> binding.snackbar(
                    action
                )

                else -> Timber.i("Implementation for observed action $action not supported")
            }
        })

        mainVm.matchToggleEvent.observe(viewLifecycleOwner) {
            binding.fRulesLExtra.root.visibility = if (Toggle.AdvancedRules.isEnabled) VISIBLE else GONE
        }

        // Add toolbar menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean { // Navbar navigation listener
                return view?.findNavController()?.let { NavigationUI.onNavDestinationSelected(menuItem, it) } ?: false
            }
        })

//        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                SnookerBoardTheme {
                    GenericSurface {
//                        FragmentRules()
                    }
                }
            }
        }
    }
}


@Composable
fun FragmentRules(
    navController: NavController,
    mainVm: MainViewModel
) {
    val rulesVm: RulesViewModel = viewModel(factory = GenericViewModelFactory())
    FragmentColumn {
                val focusManager = LocalFocusManager.current
        Row(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.smallMedium)
            ) {
                TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_a_label))
                AppTextField(
                    text = rulesVm.player01FirstName,
                    placeholder = "First Name",
                    onChange = { rulesVm.player01FirstName = it },
                    imeAction = ImeAction.Next,
                    keyBoardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                AppTextField(
                    text = rulesVm.player01LastName,
                    placeholder = "Last Name",
                    onChange = { rulesVm.player01LastName = it },
                    imeAction = ImeAction.Next,
                    keyBoardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = MaterialTheme.spacing.smallMedium)
            ) {
                TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_tv_player_b_label))
                AppTextField(
                    text = rulesVm.player02FirstName,
                    placeholder = "First Name",
                    onChange = { rulesVm.player02FirstName = it },
                    imeAction = ImeAction.Next,
                    keyBoardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                AppTextField(
                    text = rulesVm.player02LastName,
                    placeholder = "Last Name",
                    onChange = { rulesVm.player02LastName = it },
                    imeAction = ImeAction.Done,
                )
            }
        }
        TextNavParagraphSubTitle(stringResource(R.string.l_rules_main_hint_name_last))
        AndroidView( // Number Picker
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, 0.dp, 0.dp),
            factory = { context ->
                NumberPicker(context).apply {
                    dividerColor = Color.WHITE
                    setDividerDistance(360)
                    orientation = LinearLayout.HORIZONTAL
                    selectedTextColor = Color.WHITE
                    textColor = Color.WHITE
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                    setOnValueChangedListener { _, _, newVal ->
                        rulesVm.updateAction(RULES_AVAILABLE_FRAMES, newVal)
                    }
                }
            },
            update = {
            })
        RuleSelectionItem(stringResource(R.string.l_rules_main_tv_breaks_first_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_tv_player_a_label),
                rulesVm = rulesVm,
                action = RULES_STARTING_PLAYER,
                value = 0
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_btn_breaks_random),
                rulesVm = rulesVm,
                action = RULES_STARTING_PLAYER,
                value = 2
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_main_tv_player_a_label),
                rulesVm = rulesVm,
                action = RULES_STARTING_PLAYER,
                value = 1
            )
        }
        RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_reds_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_reds_six),
                rulesVm = rulesVm,
                action = RULES_AVAILABLE_REDS,
                value = 6,
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_reds_ten),
                rulesVm = rulesVm,
                action = RULES_AVAILABLE_REDS,
                value = 10
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_reds_fifteen),
                rulesVm = rulesVm,
                action = RULES_AVAILABLE_REDS,
                value = 15
            )
        }
        RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_foul_modifier_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_foul_one),
                rulesVm = rulesVm,
                action = RULES_FOUL_MODIFIER,
                value = -3,
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_foul_two),
                rulesVm = rulesVm,
                action = RULES_FOUL_MODIFIER,
                value = -2
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_foul_three),
                rulesVm = rulesVm,
                action = RULES_FOUL_MODIFIER,
                value = -1
            )
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_foul_four),
                rulesVm = rulesVm,
                action = RULES_FOUL_MODIFIER,
                value = 0
            )
        }
        RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_handicap_frame_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_handicap_frame_less),
                rulesVm = rulesVm,
                action = RULES_HANDICAP_FRAME,
                value = -10,
            )
            RulesHandicapLabel(rulesVm = rulesVm, action = RULES_HANDICAP_FRAME)
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_handicap_frame_more),
                rulesVm = rulesVm,
                action = RULES_HANDICAP_FRAME,
                value = 10
            )
        }
        RuleSelectionItem(stringResource(R.string.l_rules_extra_tv_handicap_match_label)) {
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_handicap_match_less),
                rulesVm = rulesVm,
                action = RULES_HANDICAP_MATCH,
                value = -1,
            )
            RulesHandicapLabel(rulesVm = rulesVm, action = RULES_HANDICAP_MATCH)
            ButtonStandardHoist(
                text = stringResource(R.string.l_rules_extra_btn_handicap_match_more),
                rulesVm = rulesVm,
                action = RULES_HANDICAP_MATCH,
                value = 1
            )
        }
        Button(
            shape = RoundedCornerShape(50.dp),
            onClick = {
                mainVm.startMatchQuery()
            },
        ) {
            Text(text = "Start Match")
        }
    }
}