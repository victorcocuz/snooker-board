package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.admob.AdMob
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchRules.RULES
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber


class GameFragment : Fragment() {
    private val gameVm: GameViewModel by viewModels()
    private val dialogsVm: DialogViewModel by activityViewModels()
    private val matchVm: MatchViewModel by activityViewModels()
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        postponeEnterTransition() // Wait for data to load before displaying fragment

        // AdMob
        val adMob = AdMob(this.requireContext())
        adMob.loadInterstitialAd()
        adMob.interstitialAdSetContentCallbacks()

        // Start new match or load existing match
        when (RULES.matchState) {
            IDLE -> gameVm.resetMatch()
            IN_PROGRESS -> gameVm.loadMatch(matchVm.displayFrame.value)
            else -> Timber.e("No implementation for action ${RULES.matchState} at this point")
        }

        // Bind view elements
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.apply { // Bind all layouts
            lifecycleOwner = viewLifecycleOwner

            // Bind top and score layouts
            fGameLayoutTop.apply {
                varPlayerTagType = PlayerTagType.MATCH
                varMatchVm = this@GameFragment.matchVm
            }
            fGameLayoutScore.apply {
                varMatchVm = this@GameFragment.matchVm
            }

            // Bind break layout
            varMatchVm = this@GameFragment.matchVm
            fGameBreakRv.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
            }

            // Bind buttons
            fGameLayoutActionButtons.apply {
                varGameVm = this@GameFragment.gameVm
                varMatchVm = this@GameFragment.matchVm
                fGameBallsLl.layoutParams.height =
                    requireContext().getFactoredDimen(FACTOR_BALL_MATCH) + resources.getDimension(R.dimen.margin_layout_offset).toInt() * 2
                fGameBallsRv.apply {
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = null
                    adapter = BallAdapter( // Create a ball adapter for the balls recycler view
                        BallListener { ball -> // Add a listener to the adapter to handle clicking, which will check whether a ball/freeball was clicked
                            requireActivity().invalidateOptionsMenu()
                            gameVm.assignPot(TYPE_HIT, ball)
                        }, matchVm.displayFrame, BallAdapterType.MATCH)
                }
                fGameBallMiss.apply {
                    layoutParams.width = context.getFactoredDimen(FACTOR_BALL_MATCH)
                    layoutParams.height = context.getFactoredDimen(FACTOR_BALL_MATCH)
                    setPadding(FACTOR_BALL_MATCH)
                }
            }
        }

        // VM Observers
//        getNavigationResult<MatchAction>(null, "matchAction") { result ->
//            Timber.e("method result is $result")
//            when (result) {
//                else -> Timber.e("not implemented")
//            }
//        }
        gameVm.apply {
            eventFrameAction.observe(viewLifecycleOwner, EventObserver { action ->
                Timber.i("Observed eventFrameAction: $action")
                when (action) {
                    // Called from gameVm
                    FRAME_UPDATED -> {
                        requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
                        matchVm.updateFrame(DomainFrame(RULES.frameCount, ballStack, score, frameStack, actionLogs, RULES.frameMax))
                    }
                    SNACKBAR_NO_BALL -> binding.fGameCoordLayout.snackbar(getString(R.string.toast_game_no_balls_left))

                    // Dialog navigation
                    TRANSITION_TO_FRAGMENT -> matchVm.transitionToFragment(this@GameFragment, 200)
                    FOUL_DIALOG -> navigate(GameFragmentDirections.foulDialogFrag())
                    FRAME_LOG_ACTIONS_DIALOG, FRAME_RESPOT_BLACK_DIALOG, FRAME_RERACK_DIALOG, FRAME_ENDING_DIALOG, MATCH_ENDING_DIALOG, MATCH_CANCEL_DIALOG -> {
                        val actions = action.getListOfDialogActions(score.isMatchEnding(), isFrameMathematicallyOver())
                        navigate(GameFragmentDirections.genDialogFrag(actions[0], actions[1], actions[2]))
                    }
                    else -> matchVm.onEventMatchAction(action)
                }
            })
        }
        matchVm.apply {
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { action ->
                Timber.i("Observed eventMatchAction: $action")
                when (action) {
                    // Confirmed from dialogs
                    FOUL_CONFIRM -> {
                        gameVm.assignPot(action.getPotType(), dialogsVm.ballClicked.value!!, dialogsVm.actionClicked.value!!)
                        dialogsVm.resetFoul()
                    }
                    FRAME_TO_END, FRAME_ENDED, MATCH_TO_END, MATCH_ENDED -> gameVm.endFrame(action)
                    FRAME_RERACK, FRAME_START_NEW -> {
                        gameVm.resetFrame(action)
                        adMob.showInterstitialAd()
                    }
                    MATCH_ENDED_DISCARD_FRAME -> {
                        deleteCrtFrameFromDb()
                        gameVm.onEventFrameAction(NAV_TO_POST_MATCH)
                    }
                    NAV_TO_POST_MATCH -> {
                        updateState(POST_MATCH)
                        adMob.showInterstitialAd()
                        navigate(GameFragmentDirections.postGameFrag())
                    }
                    MATCH_CANCEL -> {
                        adMob.showInterstitialAd()
                        matchVm.deleteMatchFromDb()
                        navigate(GameFragmentDirections.playFrag())
                    }
                    FRAME_LOG_ACTIONS -> emailLogs()

                    // Called from matchVm to be added to job queue
                    FRAME_RESPOT_BLACK, FRAME_FREE_AVAILABLE, FRAME_UNDO -> gameVm.assignPot(action.getPotType())
                    else -> Timber.i("Implementation for observed action $action not supported")
                }
            })
        }

        // Menu items - tied to lifecycle owner; The lifecycle state indicates when the menu should be visible
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_game_overflow, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                gameVm.apply {
                    when (menuItem.itemId) {
                        R.id.menu_item_log -> onEventFrameAction(FRAME_LOG_ACTIONS_DIALOG)
                        R.id.menu_item_undo -> {
                            if (frameStack.isFrameInProgress()) assignPot(null)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_undo))
                        }
                        R.id.menu_item_add_red -> {
                            if (ballStack.isAddRedAvailable()) assignPot(TYPE_ADDRED)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_add_red))
                        }
                        R.id.menu_item_remove_red -> {
                            if (isRemoveRedAvailable()) assignPot(TYPE_REMOVE_RED)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_remove_red))
                        }
                        R.id.menu_item_remove_color -> {
                            if (isRemoveColorAvailable()) assignPot(TYPE_REMOVE_COLOR)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_remove_color))
                        }
                        R.id.menu_item_rerack -> {
                            if (frameStack.isFrameInProgress()) onEventFrameAction(FRAME_RERACK_DIALOG)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_rerack))
                        }
                        R.id.menu_item_concede_frame -> {
                            if (!score.isFrameEqual()) onEventFrameAction(FRAME_ENDING_DIALOG)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_concede_frame))
                        }
                        R.id.menu_item_concede_match -> {
                            if (!score.isFrameAndMatchEqual()) onEventFrameAction(MATCH_ENDING_DIALOG)
                            else binding.fGameCoordLayout.snackbar(getString(R.string.snackbar_f_game_concede_match))
                        }
                        R.id.menu_item_cancel_match -> {
                            if (score.isMatchInProgress()) onEventFrameAction(MATCH_CANCEL_DIALOG)
                            else matchVm.onEventMatchAction(MATCH_CANCEL)
                        }
                        else -> return false
                    }
                }
                requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
                return true
            }

            override fun onPrepareMenu(menu: Menu) { // Set the enabled value of menu items to match the match circumstances
                gameVm.apply { // the menu dropdown is available when frame isn't updating, then apply rules for each button
                    for (item in menu.children) item.setItemActive(!(gameVm.isUpdateInProgress.value ?: false))
                    menu.findItem(R.id.menu_item_undo).setItemActive(frameStack.isFrameInProgress())
                    menu.findItem(R.id.menu_item_add_red).setItemActive(ballStack.isAddRedAvailable())
                    menu.findItem(R.id.menu_item_remove_red).setItemActive(isRemoveRedAvailable())
                    menu.findItem(R.id.menu_item_remove_color).setItemActive(isRemoveColorAvailable())
                    menu.findItem(R.id.menu_item_rerack).setItemActive(frameStack.isFrameInProgress())
                    menu.findItem(R.id.menu_item_concede_frame).setItemActive(!score.isFrameEqual())
                    menu.findItem(R.id.menu_item_concede_match).setItemActive(!score.isFrameAndMatchEqual())
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {} // Disable back pressing
        })

        return binding.root
    }
}

