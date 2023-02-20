package com.quickpoint.snookerboard.ui.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.admob.AdMob
import com.quickpoint.snookerboard.base.EventObserver
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.PotType.TYPE_ADDRED
import com.quickpoint.snookerboard.domain.PotType.TYPE_REMOVE_COLOR
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber


class GameFragment : Fragment() {
    private lateinit var gameVm: GameViewModel
    private val dialogVm: DialogViewModel by activityViewModels()
    private val mainVm: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        postponeEnterTransition() // Wait for data to load before displaying fragment
        gameVm = ViewModelProvider(this, GenericViewModelFactory())[GameViewModel::class.java]

//         AdMob
        val adMob = AdMob(this.requireContext())
        adMob.loadInterstitialAd()
        adMob.interstitialAdSetContentCallbacks()

//         Bind view elements
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.apply { // Bind all layouts
            lifecycleOwner = viewLifecycleOwner
            fGameLScoreBreakdown.apply {
                varGameVm = gameVm
                fGameRvBreak.apply {
                    adapter = BreakAdapter(requireActivity())
                    itemAnimator = null
                }
            }
        }

//         Observers
        gameVm.apply {
            eventGameAction.observe(viewLifecycleOwner, EventObserver { action ->
                Timber.i("Observed eventFrameAction: $action")
                when (action) {
                    // Directly observed from gameVm
                    TRANSITION_TO_FRAGMENT -> mainVm.turnOffSplashScreen(200)
                    FRAME_UPDATED -> {
                        requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
                        Timber.i(getString(R.string.helper_update_frame_info))
                    }
                    SNACK_UNDO, SNACK_ADD_RED, SNACK_REMOVE_COLOR, SNACK_FRAME_RERACK_DIALOG,
                    SNACK_FRAME_ENDING_DIALOG, SNACK_MATCH_ENDING_DIALOG, SNACK_NO_BALL,
                    -> {
                    } // snackbar action

                    // Dialogs relating
//                    FOUL_DIALOG -> navigate(GameFragmentDirections.foulDialogFrag())
                    FOUL_CONFIRM -> {
                        gameVm.assignPot(PotType.TYPE_FOUL, dialogVm.ballClicked.value!!, PotAction.FIRST) // Temp Pot Action
                        dialogVm.onDismissFoulDialog()
                    }
                    FRAME_LOG_ACTIONS_DIALOG, FRAME_LAST_BLACK_FOULED_DIALOG, FRAME_RESPOT_BLACK_DIALOG, FRAME_RERACK_DIALOG, FRAME_ENDING_DIALOG, MATCH_ENDING_DIALOG,
                    MATCH_CANCEL_DIALOG, FRAME_MISS_FORFEIT_DIALOG,
                    -> {
                        val actions =
                            action.getListOfDialogActions(score.isMatchEnding(), score.isNoFrameFinished(), isFrameMathematicallyOver())
//                        navigate(GameFragmentDirections.genDialogFrag(actions[0], actions[1], actions[2]))
                    }
                    FRAME_LOG_ACTIONS -> gameVm.emailLogs(requireContext())
                    FRAME_FREE_ACTIVE, FRAME_UNDO, FRAME_REMOVE_RED, FRAME_LAST_BLACK_FOULED, FRAME_RESPOT_BLACK -> gameVm.assignPot(action.getPotType())
                    FRAME_MISS_FORFEIT -> gameVm.onEventGameAction(
                        action.queryEndFrameOrMatch(
                            score.isMatchEnding(),
                            isFrameMathematicallyOver()
                        )
                    )
                    FRAME_TO_END, FRAME_ENDED, MATCH_TO_END, MATCH_ENDED -> gameVm.endFrame(action)
                    FRAME_RERACK, FRAME_START_NEW -> {
                        adMob.showInterstitialAd()
                        gameVm.resetFrame(action)
                    }
                    MATCH_ENDED_DISCARD_FRAME -> {
                        gameVm.deleteCrtFrameFromDb()
                        gameVm.onEventGameAction(NAV_TO_POST_MATCH)
                    }
                    NAV_TO_POST_MATCH -> {
                        Settings.matchState = MatchState.SUMMARY
//                        navigate(GameFragmentDirections.summaryFrag(), adMob)
                    }
                    MATCH_CANCEL -> {
                        gameVm.deleteMatchFromDb()
//                        navigate(GameFragmentDirections.rulesFrag(), adMob)
                    }
                    else -> Timber.i("No implementation for observed action $action")
                }
            })
        }

        // Menu items - tied to lifecycle owner; The lifecycle state indicates when the menu should be visible
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_game_overflow, menu)
                menu.forEach { it.onMenuItemLongClickListener(menu) {} }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                gameVm.apply {
                    when (menuItem.itemId) {
                        R.id.menu_item_log -> onEventGameAction(FRAME_LOG_ACTIONS_DIALOG)
                        R.id.menu_item_undo ->
                            if (frameStack.isFrameInProgress()) assignPot(null) else onEventGameAction(SNACK_UNDO)
                        R.id.menu_item_add_red ->
                            if (ballStack.isAddRedAvailable()) assignPot(TYPE_ADDRED) else onEventGameAction(SNACK_ADD_RED)
                        R.id.menu_item_remove_color ->
                            if (isRemoveColorAvailable()) assignPot(TYPE_REMOVE_COLOR) else onEventGameAction(SNACK_REMOVE_COLOR)
                        R.id.menu_item_rerack ->
                            onEventGameAction(if (frameStack.isFrameInProgress()) FRAME_RERACK_DIALOG else SNACK_FRAME_RERACK_DIALOG)
                        R.id.menu_item_concede_frame ->
                            onEventGameAction(if (!score.isFrameEqual()) FRAME_ENDING_DIALOG else SNACK_FRAME_ENDING_DIALOG)
                        R.id.menu_item_concede_match ->
                            onEventGameAction(if (!score.isFrameAndMatchEqual()) MATCH_ENDING_DIALOG else SNACK_MATCH_ENDING_DIALOG)
                        R.id.menu_item_cancel_match ->
                            onEventGameAction(if (score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL)
                        else -> return false
                    }
                }
                requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
                return true
            }

            override fun onPrepareMenu(menu: Menu) { // Set the enabled value of menu items to match the match circumstances
                gameVm.apply { // the menu dropdown is available when frame isn't updating, then apply rules for each button
                    menu.findItem(R.id.menu_item_undo).setItemActive(frameStack.isFrameInProgress())
                    menu.findItem(R.id.menu_item_add_red).setItemActive(ballStack.isAddRedAvailable())
                    menu.findItem(R.id.menu_item_remove_color).setItemActive(isRemoveColorAvailable())
                    menu.findItem(R.id.menu_item_rerack).setItemActive(frameStack.isFrameInProgress())
                    menu.findItem(R.id.menu_item_concede_frame).setItemActive(!score.isFrameEqual())
                    menu.findItem(R.id.menu_item_concede_match).setItemActive(!score.isFrameAndMatchEqual())
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Handle back button pressing
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameVm.onEventGameAction(if (gameVm.score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL)
            }
        })

        return binding.root
    }
}

