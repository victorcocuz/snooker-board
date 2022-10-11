package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.*
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import com.quickpoint.snookerboard.utils.MatchAction.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)
    private val gameVm: GameViewModel by viewModels()
    private val dialogsVm: DialogViewModel by activityViewModels()
    private val matchVm: MatchViewModel by activityViewModels()
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding
    private var scrollHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        ballAdapter = BallAdapter( // Create a ball adapter for the balls recycler view
            BallListener { ball -> // Add a listener to the adapter to handle clicking, which will check whether a ball/freeball was clicked
                requireActivity().invalidateOptionsMenu()
                gameVm.handlePot(if (ball is FREEBALL) FREE else HIT(ball))
            },
            matchVm.displayFrame,
            BallAdapterType.MATCH
        )

        // Bind all required elements from the view
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.apply { // Bind all layouts
            lifecycleOwner = viewLifecycleOwner

            // Bind top and score layouts
            fragGameLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varPlayerTagType = PlayerTagType.MATCH
                varMatchVm = this@GameFragment.matchVm
            }
            fragGameLayoutScore.apply {
                varMatchVm = this@GameFragment.matchVm
            }

            // Bind break layout
            fragGameLayoutBreak.apply {
                varMatchVm = this@GameFragment.matchVm

                // Assign ghost & scroll view height that lines up with the top of the action layout
                gameGhostLayout.viewTreeObserver.addOnGlobalLayoutListener {
                    gameScrollView.assignScrollHeight(scrollHeight, gameGhostLayout.measuredHeight)
                }
                gameScrollView.viewTreeObserver.addOnGlobalLayoutListener {
                    gameScrollView.assignScrollHeight(scrollHeight, gameScrollView.measuredHeight)
                    gameScrollView.scrollToBottom()
                }
                gameBreakRv.apply {
                    adapter = BreakAdapter(requireActivity())
                    itemAnimator = null
                    (layoutManager as LinearLayoutManager).stackFromEnd = true
                }
            }

            // Bind game query layout
            fragGameLayoutQuery.apply {
                varMatchVm = this@GameFragment.matchVm
            }

            // Bind buttons
            fragGameLayoutActionButtons.apply {
                varGameVm = this@GameFragment.gameVm
                varMatchVm = this@GameFragment.matchVm
                fragGameBallsLl.layoutParams.height =
                    requireContext().getFactoredDimen(BALL_HEIGHT_FACTOR_MATCH_ACTION)
                +resources.getDimension(R.dimen.margin_layout_offset).toInt() * 2
                fragGameBallsRv.apply {
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = null
                    adapter = ballAdapter
                }
            }
        }

        binding.apply { // Start or load match
            if (getSharedPref().isMatchInProgress()) {
                setQueryMatchVisibility()
                matchVm.loadMatchAPointToCrtFrame()
            } else {
                setPlayMatchVisibility()
                matchVm.startNewMatch()
            }
        }

        // VM Observers
        gameVm.apply {
            eventFrameUpdated.observe(viewLifecycleOwner, EventObserver { // Reset the menu every time the frame has been updated
                requireActivity().invalidateOptionsMenu()
                matchVm.updateFrameInfo(ballStack, frameStack)
                Timber.i(getString(R.string.helper_update_frame_info))
            })
        }
        matchVm.apply {
            crtFrame.observe(viewLifecycleOwner) { crtFrame -> // Load mach once frame is pointing correctly from the database
                if (isUpdateFrame.value == true) {
                    gameVm.loadMatchBLoadFrame(crtFrame?.asDomainFrame())
                    loadMatchCDeleteCrtFrame()
                }
            }
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                Timber.i("observed eventMatchAction: $matchAction")
                when (matchAction) {
                    MATCH_CONTINUE -> binding.setPlayMatchVisibility()
                    MATCH_CANCEL_DIALOG -> findNavController().navigate(
                        GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(CLOSE_DIALOG, CLOSE_DIALOG, MATCH_CANCEL)
                    )
                    MATCH_CANCEL -> { // On a match cancel, cancel match and go back to play fragment
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
                        cancelMatch()
                    }
                    FRAME_RESET_DIALOG -> findNavController().navigate(
                        GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(CLOSE_DIALOG, CLOSE_DIALOG, FRAME_RESET)
                    )
                    FRAME_SAVE -> gameVm.saveFrame()
                    FRAME_RESET -> gameVm.resetFrame()
                    FRAME_ENDED_DIALOG, MATCH_ENDED_DIALOG -> findNavController().navigate(
                        GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                            CLOSE_DIALOG,
                            if (queryEndFrameOrMatch(matchAction) == MATCH_TO_END_DIALOG) MATCH_ENDED_DISCARD_FRAME_DIALOG else CLOSE_DIALOG,
                            queryEndFrameOrMatch(matchAction)
                        )
                    )
                    FRAME_TO_END_DIALOG, FRAME_ENDED -> matchVm.saveAndStartNewFrame()
                    MATCH_TO_END_DIALOG, MATCH_ENDED, MATCH_ENDED_DISCARD_FRAME_DIALOG -> { // End match
                        matchVm.matchEnded(matchAction)
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                    }
                    FOUL_DIALOG -> { // Navigate to foul dialog when queried
                        dialogsVm.resetFoul()
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
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
                when (menuItem.itemId) {
                    R.id.match_action_undo -> gameVm.handleUndo() // DomainPot is irrelevant here
                    R.id.match_action_add_red -> gameVm.handlePot(ADDRED)
                    R.id.match_action_remove_red -> gameVm.handlePot(REMOVERED)
                    R.id.match_action_rerack -> matchVm.assignEventMatchAction(FRAME_RESET_DIALOG)
                    R.id.match_action_cancel_match -> matchVm.assignEventMatchAction(
                        if (matchVm.displayFrame.value?.frameStack?.size!! > 0) MATCH_CANCEL_DIALOG else MATCH_CANCEL
                    )
                    R.id.match_action_concede_frame -> matchVm.assignEventMatchAction(FRAME_ENDED_DIALOG)
                    R.id.match_action_concede_match -> matchVm.assignEventMatchAction(MATCH_ENDED_DIALOG)
                    else -> return false
                }
                requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
                return true
            }

            override fun onPrepareMenu(menu: Menu) { // Set the enabled value of menu items to match the match circumstances
                matchVm.displayFrame.value?.apply { // the menu dropdown is available when frame isn't updating, then apply rules for each button
                    for (item in menu.children) item.isEnabled = !(gameVm.isUpdateInProgress.value ?: false)

                    menu.findItem(R.id.match_action_undo).apply {
                        isEnabled = frameStack.size > 0
                        setStateOpacity()
                    }
                    menu.findItem(R.id.match_action_add_red).isEnabled = ballStack.size in (10..36).filter { it % 2 != 0 }
                    menu.findItem(R.id.match_action_remove_red).isEnabled =
                        ballStack.size in (10..36).filter { it % 2 != 0 } && !FREEBALLINFO.isVisible
                    menu.findItem(R.id.match_action_rerack).isEnabled = frameStack.size > 0
                    menu.findItem(R.id.match_action_concede_frame).isEnabled = getFrameScoreDiff() != 0
                    menu.findItem(R.id.match_action_concede_match).isEnabled = getFrameScoreDiff() != 0 || getMatchScoreDiff() != 0
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // When pressing back, save match before killing fragment
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameFragmentScope.launch {
                    matchVm.saveMatch()
                }
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
            }
        })

        return binding.root
    }
}
