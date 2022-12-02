package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.forEach
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
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
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber


class GameFragment : Fragment() {
    private lateinit var gameVm: GameViewModel
    private val dialogVm: DialogViewModel by activityViewModels()
    private val matchVm: MatchViewModel by activityViewModels()
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        postponeEnterTransition() // Wait for data to load before displaying fragment
        gameVm = ViewModelProvider(this, GenericViewModelFactory(this, null))[GameViewModel::class.java]

        // AdMob
        val adMob = AdMob(this.requireContext())
        adMob.loadInterstitialAd()
        adMob.interstitialAdSetContentCallbacks()

        // Start new match or load existing match
        if (SETTINGS.matchState == IDLE) {
            gameVm.resetMatch()
            matchVm.updateState(IN_PROGRESS)
        } else matchVm.storedFrame.observe(viewLifecycleOwner, EventObserver { storedFrame ->
            gameVm.loadMatch(storedFrame)
        })

        // Bind view elements
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.apply { // Bind all layouts
            lifecycleOwner = viewLifecycleOwner

            // Bind top and score layouts
            fGameLTop.apply {
                varPlayerTagType = PlayerTagType.MATCH
                varGameVm = this@GameFragment.gameVm
            }
            fGameLScore.apply {
                varGameVm = this@GameFragment.gameVm
            }

            // Bind break layout
            varGameVm = this@GameFragment.gameVm
            fGameRvBreak.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
            }

            // Bind buttons
            fGameLActions.apply {
                varGameVm = this@GameFragment.gameVm
                lGameActionsLlBalls.layoutParams.height =
                    requireContext().getFactoredDimen(FACTOR_BALL_MATCH) + resources.getDimension(R.dimen.margin_layout_offset).toInt() * 2
                lGameActionsRvBalls.apply {
                    layoutManager = object : LinearLayoutManager(activity, HORIZONTAL, false) {
                        override fun canScrollHorizontally() = false
                    }
                    itemAnimator = null
                    adapter = BallAdapter( // Create a ball adapter for the balls recycler view
                        BallListener { ball -> // Add a listener to the adapter to handle clicking, which will check whether a ball/freeball was clicked
                            requireActivity().invalidateOptionsMenu()
                            gameVm.assignPot(TYPE_HIT, ball)
                        }, gameVm.displayFrame, BallAdapterType.MATCH)
                }
                lGameActionsFlBallMiss.apply {
                    layoutParams.width = context.getFactoredDimen(FACTOR_BALL_MATCH)
                    layoutParams.height = context.getFactoredDimen(FACTOR_BALL_MATCH)
                    setPadding(FACTOR_BALL_MATCH)
                }
            }
        }

        // Observers
        gameVm.apply {
            eventGameAction.observe(viewLifecycleOwner, EventObserver { action ->
                Timber.i("Observed eventFrameAction: $action")
                when (action) {
                    // Directly observed from gameVm
                    TRANSITION_TO_FRAGMENT -> matchVm.transitionToFragment(this@GameFragment, 200)
                    FRAME_UPDATED -> {
                        requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
                        Timber.i(getString(R.string.helper_update_frame_info))
                    }
                    SNACKBAR_NO_BALL -> binding.snackbar(getString(R.string.toast_f_game_no_balls_left))

                    // Dialogs relating
                    FOUL_DIALOG -> navigate(GameFragmentDirections.foulDialogFrag())
                    FOUL_CONFIRM -> {
                        gameVm.assignPot(TYPE_FOUL, dialogVm.ballClicked.value!!, dialogVm.actionClicked.value!!)
                        dialogVm.resetFoul()
                    }
                    FRAME_LOG_ACTIONS_DIALOG, FRAME_RESPOT_BLACK_DIALOG, FRAME_RERACK_DIALOG, FRAME_ENDING_DIALOG,
                    MATCH_ENDING_DIALOG, MATCH_CANCEL_DIALOG, FRAME_MISS_FORFEIT_DIALOG -> {
                        val actions = action.getListOfDialogActions(score.isMatchEnding(), isFrameMathematicallyOver())
                        navigate(GameFragmentDirections.genDialogFrag(actions[0], actions[1], actions[2]))
                    }
                    FRAME_LOG_ACTIONS -> matchVm.emailLogs()
                    FRAME_FREE_AVAILABLE, FRAME_UNDO, FRAME_RESPOT_BLACK -> gameVm.assignPot(action.getPotType())
                    FRAME_MISS_FORFEIT -> gameVm.onEventGameAction(action.queryEndFrameOrMatch(score.isMatchEnding(), isFrameMathematicallyOver()))
                    FRAME_TO_END, FRAME_ENDED, MATCH_TO_END, MATCH_ENDED -> gameVm.endFrame(action)
                    FRAME_RERACK, FRAME_START_NEW -> {
                        adMob.showInterstitialAd()
                        gameVm.resetFrame(action)
                    }
                    MATCH_ENDED_DISCARD_FRAME -> {
                        matchVm.deleteCrtFrameFromDb()
                        gameVm.onEventGameAction(NAV_TO_POST_MATCH)
                    }
                    NAV_TO_POST_MATCH -> {
                        matchVm.updateState(POST_MATCH)
                        navigate(GameFragmentDirections.summaryFrag(), adMob)
                    }
                    MATCH_CANCEL -> {
                        matchVm.deleteMatchFromDb()
                        navigate(GameFragmentDirections.rulesFrag(), adMob)
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
                        R.id.menu_item_undo -> {
                            if (frameStack.isFrameInProgress()) assignPot(null)
                            else binding.snackbar(getString(R.string.snackbar_f_game_undo))
                        }
                        R.id.menu_item_add_red -> {
                            if (ballStack.isAddRedAvailable()) assignPot(TYPE_ADDRED)
                            else binding.snackbar(getString(R.string.snackbar_f_game_add_red))
                        }
                        R.id.menu_item_remove_red -> {
                            if (isRemoveRedAvailable()) assignPot(TYPE_REMOVE_RED)
                            else binding.snackbar(getString(R.string.snackbar_f_game_remove_red))
                        }
                        R.id.menu_item_remove_color -> {
                            if (isRemoveColorAvailable()) assignPot(TYPE_REMOVE_COLOR)
                            else binding.snackbar(getString(R.string.snackbar_f_game_remove_color))
                        }
                        R.id.menu_item_rerack -> {
                            if (frameStack.isFrameInProgress()) onEventGameAction(FRAME_RERACK_DIALOG)
                            else binding.snackbar(getString(R.string.snackbar_f_game_rerack))
                        }
                        R.id.menu_item_concede_frame -> {
                            if (!score.isFrameEqual()) onEventGameAction(FRAME_ENDING_DIALOG)
                            else binding.snackbar(getString(R.string.snackbar_f_game_concede_frame))
                        }
                        R.id.menu_item_concede_match -> {
                            if (!score.isFrameAndMatchEqual()) onEventGameAction(MATCH_ENDING_DIALOG)
                            else binding.snackbar(getString(R.string.snackbar_f_game_concede_match))
                        }
                        R.id.menu_item_cancel_match -> {
                            onEventGameAction(if (score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL)
                        }
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
                    menu.findItem(R.id.menu_item_remove_red).setItemActive(isRemoveRedAvailable())
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