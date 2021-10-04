package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickpoint.snookerboard.GenericEventsViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.utils.*
import kotlinx.android.synthetic.main.fragment_game_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding
    private var scrollHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        ballAdapter = BallAdapter( // Create a ball adapter for the balls recycler view
            BallListener { ball -> // Add a listener to the adapter to handle clicking
                requireActivity().invalidateOptionsMenu()
                gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.HIT(ball), null, null)
            },
            gameViewModel.displayFrame,
            BallAdapterType.MATCH
        )

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = this@GameFragment
            setHasOptionsMenu(true)
            varGameViewModel = this@GameFragment.gameViewModel

            fragGameLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varPlayerTagType = PlayerTagType.MATCH
                varGameViewModel = this@GameFragment.gameViewModel
                varApplication = requireActivity().application
            }

            fragGameLayoutScore.apply {
                varGameViewModel = this@GameFragment.gameViewModel
                varApplication = requireActivity().application
            }

            fragGameGhostLayout.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action layout
                fragGameScrollView.assignScrollHeight(scrollHeight, fragGameGhostLayout.measuredHeight)
            }
            fragGameScrollView.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action layout
                fragGameScrollView.assignScrollHeight(scrollHeight, fragGameScrollView.measuredHeight)
                fragGameScrollView.scrollToBottom()
            }

            fragGameBreakRv.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
                (layoutManager as LinearLayoutManager).stackFromEnd = true
            }

            fragGameLayoutActionButtons.apply {
                gameViewModel = this@GameFragment.gameViewModel
                genericEventsViewModel = eventsViewModel
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
        gameViewModel.apply {
            // Match actions relating generic dialog
            eventGameAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                findNavController().navigate( // Assign match actions for generic dialog, then navigate
                    GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                        MatchAction.NO_ACTION,
                        if (matchAction == MatchAction.MATCH_END_QUERY) MatchAction.MATCH_END_CONFIRMED_AND_DISCARD_CURRENT_FRAME else MatchAction.NO_ACTION,
                        matchAction
                    )
                )
            })
            eventFrameResetComplete.observe(viewLifecycleOwner, EventObserver {
                requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
            })
        }
        eventsViewModel.apply {
            eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                gameViewModel.apply {
                    when (matchAction) {
                        // Match actions foul related
                        MatchAction.FOUL_DIALOG_QUERIED -> {
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
                        }
                        MatchAction.FOUL_CONFIRMED -> {
                            handleFrameEvent(
                                FrameEvent.HANDLE_FOUL,
                                getFoul(),
                                eventsViewModel.isRemoveRed.value!!,
                                eventsViewModel.isFreeBall.value!!
                            )
                            eventsViewModel.resetFoul()
                        }
                        in listOf(MatchAction.FRAME_END_QUERY, MatchAction.FRAME_END_CONFIRMED) -> {
                            saveAndStartNewFrame()
                        }

                        // Match actions that require navigation
                        in listOf(
                            MatchAction.MATCH_END_QUERY,
                            MatchAction.MATCH_END_CONFIRMED,
                            MatchAction.MATCH_END_CONFIRMED_AND_DISCARD_CURRENT_FRAME
                        ) -> {
                            matchEnded(matchAction) // Feed the mach action in the game view model to assess whether frame should be kept
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                        }
                        MatchAction.MATCH_CANCEL -> { // On a match cancel, cancel match and go back to play fragment
                            cancelMatch()
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
                        }
                        else -> {
                        }
                    }
                }
            })
        }

        // when pressing back, save match before killing fragment
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameFragmentScope.launch {
                    gameViewModel.saveMatch()
                }
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.match_action_undo -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_UNDO, null, null, null)
            R.id.match_action_add_red -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.ADDRED, null, null)
            R.id.match_action_rerack -> gameViewModel.resetFrame()
            R.id.match_action_concede_frame -> gameViewModel.queryEndFrame()
            R.id.match_action_cancel_match -> gameViewModel.assignEventGameAction(MatchAction.MATCH_CANCEL)
            R.id.match_action_concede_match -> gameViewModel.queryEndMatch()
        }
        requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) { // Buttons cannot be enabled while update is in progress
        gameViewModel.displayFrame.value?.apply {
            menu.findItem(R.id.match_action_undo).apply {
                isEnabled = (frameStack.size) > 0 && !(gameViewModel.isFrameUpdateInProgress.value ?: false)
                setStateOpacity()
            }
            menu.findItem(R.id.match_action_rerack).apply {
                isEnabled = !(gameViewModel.isFrameUpdateInProgress.value ?: false)
            }
            menu.findItem(R.id.match_action_add_red).apply {
                isEnabled = (ballStack.size) in (10..36).filter { it % 2 == 0 } && !(gameViewModel.isFrameUpdateInProgress.value ?: false)
            }
            menu.findItem(R.id.match_action_concede_frame).apply {
                isEnabled = getFrameScoreDiff() != 0 && !(gameViewModel.isFrameUpdateInProgress.value ?: false)
            }
            menu.findItem(R.id.match_action_concede_match).apply {
                isEnabled = getFrameScoreDiff() != 0 || getMatchScoreDiff() != 0 && !(gameViewModel.isFrameUpdateInProgress.value ?: false)
            }
        }
    }
}
