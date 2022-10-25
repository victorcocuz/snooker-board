package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.DomainBall.FREEBALL
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.domain.MatchState.*
import com.quickpoint.snookerboard.domain.asDomainPlayerScore
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber


class GameFragment : androidx.fragment.app.Fragment() {
    private val gameVm: GameViewModel by viewModels()
    private val dialogsVm: DialogViewModel by activityViewModels()
    private val matchVm: MatchViewModel by activityViewModels()
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val menuHost: MenuHost = requireActivity()
        ballAdapter = BallAdapter( // Create a ball adapter for the balls recycler view
            BallListener { ball -> // Add a listener to the adapter to handle clicking, which will check whether a ball/freeball was clicked
                requireActivity().invalidateOptionsMenu()
                gameVm.handlePot(if (ball is FREEBALL) FREE else HIT(ball))
            }, matchVm.displayFrame, BallAdapterType.MATCH)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        // Start new match or load existing match
        if (RULES.matchState == IDLE) {
            RULES.matchState = IN_PROGRESS
            gameVm.resetMatch(MATCH_START_NEW)
        }

        // Bind all required elements from the view
        binding.apply { // Bind all layouts
            lifecycleOwner = viewLifecycleOwner

            // Bind top and score layouts
            fragGameLayoutTop.apply {
                varPlayerTagType = PlayerTagType.MATCH
                varMatchVm = this@GameFragment.matchVm
            }
            fragGameLayoutScore.apply {
                varMatchVm = this@GameFragment.matchVm
            }

            // Bind break layout
            varMatchVm = this@GameFragment.matchVm
            gameBreakRv.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
            }

            // Bind buttons
            fragGameLayoutActionButtons.apply {
                varGameVm = this@GameFragment.gameVm
                varMatchVm = this@GameFragment.matchVm
                fragGameBallsLl.layoutParams.height =
                    requireContext().getFactoredDimen(BALL_HEIGHT_FACTOR_MATCH_ACTION) + resources.getDimension(R.dimen.margin_layout_offset)
                        .toInt() * 2
                fragGameBallsRv.apply {
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = null
                    adapter = ballAdapter
                }
            }
        }

        // VM Observers
        gameVm.apply {
            eventFrameAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                when (matchAction) {
                    FRAME_UPDATED -> {
                        requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
                        matchVm.updateFrameInfo( // Update frame info into matchVm
                            DomainFrame(RULES.frameCount,
                                ballStack,
                                mutableListOf(score.getFirst().asDomainPlayerScore(), score.getSecond().asDomainPlayerScore()),
                                frameStack,
                                RULES.frameMax))
                    }
                    FRAME_FREEAVAILABLE -> gameVm.handlePot(FREEAVAILABLE)
                    FRAME_UNDO -> gameVm.handleUndo()
                    FRAME_NO_BALL -> binding.fragGameCl.snackbar(getString(R.string.toast_game_no_balls_left))
                    else -> matchVm.assignEventMatchAction(matchAction)
                }
            })
        }
        matchVm.apply {
            dbCrtFrame.observe(viewLifecycleOwner) { crtFrame ->
                if (crtFrame?.frame?.frameId == RULES.frameCount && RULES.matchState == SAVED) {
                    gameVm.loadMatchBloadFrame(crtFrame.asDomainFrame())
                    matchVm.deleteCrtFrameFromDb()
                    RULES.matchState = IN_PROGRESS
                }
            }
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                Timber.i("observed eventMatchAction: $matchAction")
                when (matchAction) {
                    FRAME_RESPOT_BLACK_DIALOG -> navigate(GameFragmentDirections.genDialogFrag(IGNORE, IGNORE, FRAME_RESPOT_BLACK))
                    FRAME_RESPOT_BLACK -> gameVm.handlePot(RESPOTBLACK)
                    FRAME_RERACK_DIALOG -> navigate(GameFragmentDirections.genDialogFrag(CLOSE_DIALOG, IGNORE, FRAME_RERACK))
                    FRAME_RERACK, FRAME_START_NEW -> gameVm.resetFrame(matchAction)
                    FRAME_ENDING_DIALOG, MATCH_ENDING_DIALOG -> navigate(GameFragmentDirections.genDialogFrag(CLOSE_DIALOG,
                        IGNORE,
                        queryEndFrameOrMatch(matchAction)))
                    FRAME_TO_END, FRAME_ENDED, MATCH_TO_END, MATCH_ENDED -> {
                        gameVm.saveFrame()
                        matchVm.endFrameOrMatch(matchAction)
                    }
                    MATCH_ENDED_DISCARD_FRAME -> matchVm.endFrameOrMatch(matchAction)
                    NAV_TO_POST_MATCH -> {
                        gameVm.resetMatch(matchAction)
                        navigate(GameFragmentDirections.postGameFrag())
                    }
                    MATCH_CANCEL_DIALOG -> navigate(GameFragmentDirections.genDialogFrag(CLOSE_DIALOG, IGNORE, MATCH_CANCEL))
                    MATCH_CANCEL -> { // On a match cancel, cancel match and go back to play fragment
                        gameVm.resetMatch(matchAction)
                        matchVm.deleteMatchFromDb()
                        navigate(GameFragmentDirections.playFrag())
                    }
                    FOUL_DIALOG -> { // Navigate to foul dialog when queried
                        dialogsVm.resetFoul()
                        navigate(GameFragmentDirections.foulDialogFrag())
                    }
                    FOUL_CONFIRM -> gameVm.handlePot(dialogsVm.getFoul())
                    else -> Timber.i("Implementation for observed matchAction $matchAction not supported")
                }
            })
        }

        // Menu items - tied to lifecycle owner; The lifecycle state indicates when the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_game_overflow, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val frame = matchVm.displayFrame.value
                when (menuItem.itemId) {
                    R.id.menu_item_undo -> {
                        if (frame?.isFrameInProgress() == true) gameVm.handleUndo()
                        else binding.fragGameCl.snackbar("There are no actions to undo.")
                    } // DomainPot is irrelevant here
                    R.id.menu_item_add_red -> {
                        if (frame?.isAddRedAvailable() == true) gameVm.handlePot(ADDRED)
                        else binding.fragGameCl.snackbar("You can only pot an extra red after potting a red and if enough reds are on the table.")
                    }
                    R.id.menu_item_remove_red -> {
                        if (frame?.isRemoveRedAvailable() == true) gameVm.handlePot(REMOVERED)
                        else binding.fragGameCl.snackbar("You can only remove a red ball before declaring a foul and if there are reds available on the table")
                    }
                    R.id.menu_item_rerack -> {
                        if (frame?.isFrameInProgress() == true) matchVm.assignEventMatchAction(FRAME_RERACK_DIALOG)
                        else binding.fragGameCl.snackbar("The frame has already been racked")
                    }
                    R.id.menu_item_concede_frame -> {
                        if (frame?.isFrameEqual() == false) matchVm.assignEventMatchAction(FRAME_ENDING_DIALOG)
                        else binding.fragGameCl.snackbar("You and your opponent are tied. One must be ahead for the other one to concede frame.")
                    }
                    R.id.menu_item_concede_match -> {
                        if (frame?.isConcedeAvailable() == true) matchVm.assignEventMatchAction(MATCH_ENDING_DIALOG)
                        else binding.fragGameCl.snackbar("You and your opponent are tied. One must be ahead for the other one to concede match.")
                    }
                    R.id.menu_item_cancel_match -> matchVm.assignEventMatchAction(if (frame?.isMatchInProgress() == true) MATCH_CANCEL_DIALOG else MATCH_CANCEL)
                    else -> return false
                }
                requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
                return true
            }

            override fun onPrepareMenu(menu: Menu) { // Set the enabled value of menu items to match the match circumstances
                matchVm.displayFrame.value?.apply { // the menu dropdown is available when frame isn't updating, then apply rules for each button
                    for (item in menu.children) item.setItemActive(!(gameVm.isUpdateInProgress.value ?: false))
                    menu.findItem(R.id.menu_item_undo).setItemActive(isFrameInProgress())
                    menu.findItem(R.id.menu_item_add_red).setItemActive(isAddRedAvailable())
                    menu.findItem(R.id.menu_item_remove_red).setItemActive(isRemoveRedAvailable())
                    menu.findItem(R.id.menu_item_rerack).setItemActive(isFrameInProgress())
                    menu.findItem(R.id.menu_item_concede_frame).setItemActive(!isFrameEqual())
                    menu.findItem(R.id.menu_item_concede_match).setItemActive(!isFrameEqual() || !isMatchEqual())
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Disable back pressing
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

        return binding.root
    }
}

