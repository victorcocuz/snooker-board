package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.utils.*
import kotlinx.android.synthetic.main.fragment_game_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)
    private val gameViewModel: GameViewModel by viewModels()
    private val dialogsViewModel: DialogViewModel by activityViewModels()
    private val matchViewModel: MatchViewModel by activityViewModels()
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding
    private var scrollHeight = 0
    private var player: CurrentPlayer = CurrentPlayer.PLAYER01

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
            matchViewModel.displayFrame,
            BallAdapterType.MATCH
        )

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = this@GameFragment
            setHasOptionsMenu(true)
            varMatchViewModel = this@GameFragment.matchViewModel

            // Bind Top and score
            fragGameLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varPlayerTagType = PlayerTagType.MATCH
                varMatchViewModel = this@GameFragment.matchViewModel
                varApplication = requireActivity().application
            }
            fragGameLayoutScore.apply {
                varMatchViewModel = this@GameFragment.matchViewModel
                varApplication = requireActivity().application
            }

            // Bind body
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

            // Bind buttons
            fragGameLayoutActionButtons.apply {
                varGameViewModel = this@GameFragment.gameViewModel
                varMatchViewModel = this@GameFragment.matchViewModel
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

        // Start or load match
        GameFragmentArgs.fromBundle(requireArguments()).apply {
            Timber.i("Init: start match as $matchAction")
            when (matchAction) {
                MatchAction.MATCH_LOAD -> matchViewModel.loadMatchPartAPointToCurrentFrame()
                MatchAction.MATCH_START -> {
                    matchViewModel.startNewMatch(
                        matchNameFirstA, matchNameFirstB, matchNameLastA, matchNameLastB,
                        matchFrames,
                        matchReds,
                        matchFoul,
                        matchFirst
                    )
                    gameViewModel.resetFrame()
                }
                else -> {
                }
            }
        }

        // VM Observers
        gameViewModel.apply {
            eventGameAction.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    MatchAction.FRAME_INFO_UPDATED -> {
                        requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
                        matchViewModel.updateFrameInfo( // Push score and history update in the match vm
                            player,
                            DomainFrame(
                                rules.frameCount,
                                ballStack,
                                mutableListOf(
                                    player.getFirst().asDomainPlayerScore(),
                                    player.getSecond().asDomainPlayerScore()
                                ),
                                frameStack,
                                rules.frameMax
                            )
                        )
                    }
                    else -> {
                    }
                }
            })
        }
        matchViewModel.apply {
            crtFrame.observe(viewLifecycleOwner, { crtFrame -> // Load mach once frame is pointing correctly
                gameViewModel.loadMatchPartBLoadFrame(crtFrame?.asDomainFrame())
                loadMatchPartCDeleteCurrentFrame()
            })
            displayFrame.observe(viewLifecycleOwner, { domainFrame -> // Query end frame if last ball has been potted
                if (domainFrame.isLastBall() && !player.isFrameEqual()) matchViewModel.assignEventMatchAction(MatchAction.FRAME_END_QUERY)
            })
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                Timber.i("Observed eventMatchAction: $matchAction")
                when (matchAction) {
                    MatchAction.MATCH_CANCEL -> { // On a match cancel, cancel match and go back to play fragment
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
                        gameViewModel.resetFrame()
                    }
                    MatchAction.FRAME_RESET -> {
                        gameViewModel.resetFrame()
                    }
                    in listOf(MatchAction.FRAME_END_QUERY, MatchAction.MATCH_END_QUERY) -> findNavController().navigate(
                        GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                            MatchAction.NO_ACTION,
                            if (queryEndFrameOrMatch(matchAction) == MatchAction.MATCH_TO_END) MatchAction.MATCH_ENDED_DISCARD_FRAME else MatchAction.NO_ACTION,
                            queryEndFrameOrMatch(matchAction)
                        )
                    )
                    in listOf(MatchAction.FRAME_TO_END, MatchAction.FRAME_ENDED) -> { // Reset Frame
                        matchViewModel.saveAndStartNewFrame()
                    }
                    in listOf(
                        MatchAction.MATCH_TO_END,
                        MatchAction.MATCH_ENDED,
                        MatchAction.MATCH_ENDED_DISCARD_FRAME
                    ) -> { // End match
                        matchViewModel.matchEnded(matchAction)
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                    }
                    MatchAction.DIALOG_FOUL -> { // Navigate to foul dialog when queried
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
                    }
                    MatchAction.FOUL_CONFIRM -> { // Handle foul
                        gameViewModel.handleFrameEvent(
                            FrameEvent.HANDLE_FOUL,
                            dialogsViewModel.getFoul(),
                            dialogsViewModel.isRemoveRed.value!!,
                            dialogsViewModel.isFreeBall.value!!
                        )
                        dialogsViewModel.resetFoul()
                    }
                    else -> {
                    }
                }
            })
        }

        // when pressing back, save match before killing fragment
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameFragmentScope.launch {
                    matchViewModel.saveMatch()
                }
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
            }
        })

        return binding.root
    }

    // Menu items
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.match_action_undo -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_UNDO, null, null, null)
            R.id.match_action_add_red -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.ADDRED, null, null)
            R.id.match_action_rerack -> gameViewModel.resetFrame()
            R.id.match_action_cancel_match -> matchViewModel.cancelMatch()
            R.id.match_action_concede_frame -> matchViewModel.assignEventMatchAction(MatchAction.FRAME_END_QUERY)
            R.id.match_action_concede_match -> matchViewModel.assignEventMatchAction(MatchAction.MATCH_END_QUERY)
        }
        requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) { // Buttons cannot be enabled while update is in progress
        matchViewModel.displayFrame.value?.apply {
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
