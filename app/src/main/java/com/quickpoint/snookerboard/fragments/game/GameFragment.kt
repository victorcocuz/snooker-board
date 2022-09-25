package com.quickpoint.snookerboard.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.quickpoint.snookerboard.domain.CurrentPlayer
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.DomainPot
import com.quickpoint.snookerboard.domain.asDomainPlayerScore
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

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
        val menuHost: MenuHost = requireActivity()
        ballAdapter = BallAdapter( // Create a ball adapter for the balls recycler view
            BallListener { ball -> // Add a listener to the adapter to handle clicking
                requireActivity().invalidateOptionsMenu()
                gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.HIT(ball), null, null)
            },
            matchViewModel.displayFrame,
            BallAdapterType.MATCH
        )

        // Bind all required elements from the view
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.apply {
            lifecycleOwner = this@GameFragment
            varMatchViewModel = this@GameFragment.matchViewModel

            // Bind top and score
            fragGameLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varApplication = requireActivity().application
                varPlayerTagType = PlayerTagType.MATCH
                varMatchViewModel = this@GameFragment.matchViewModel
            }
            fragGameLayoutScore.apply {
                varMatchViewModel = this@GameFragment.matchViewModel
                varApplication = requireActivity().application
            }

            // Bind body - Assign ghost & scroll view height that lines up with the top of the action layout
            fragGameGhostLayout.viewTreeObserver.addOnGlobalLayoutListener {
                fragGameScrollView.assignScrollHeight(scrollHeight, fragGameGhostLayout.measuredHeight)
            }
            fragGameScrollView.viewTreeObserver.addOnGlobalLayoutListener {
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
                MatchAction.MATCH_LOAD -> matchViewModel.loadMatchAPointToCrtFrame()
                MatchAction.MATCH_START -> {
                    matchViewModel.startNewMatch()
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
            crtFrame.observe(viewLifecycleOwner) { crtFrame -> // Load mach once frame is pointing correctly from the database
                gameViewModel.loadMatchBLoadFrame(crtFrame?.asDomainFrame())
                loadMatchCDeleteCrtFrame()
            }
            displayFrame.observe(viewLifecycleOwner) { domainFrame -> // Query end frame if last ball has been potted
                if (domainFrame.isLastBall() && !player.isFrameEqual()) matchViewModel.assignEventMatchAction(MatchAction.FRAME_ENDED_DIALOG)
            }
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
                    in listOf(MatchAction.FRAME_ENDED_DIALOG, MatchAction.MATCH_ENDED_DIALOG) -> findNavController().navigate(
                        GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                            MatchAction.CLOSE_DIALOG,
                            if (queryEndFrameOrMatch(matchAction) == MatchAction.MATCH_TO_END_DIALOG) MatchAction.MATCH_ENDED_DISCARD_FRAME_DIALOG else MatchAction.CLOSE_DIALOG,
                            queryEndFrameOrMatch(matchAction)
                        )
                    )
                    in listOf(MatchAction.FRAME_TO_END_DIALOG, MatchAction.FRAME_ENDED) -> { // Reset Frame
                        matchViewModel.saveAndStartNewFrame()
                    }
                    in listOf(
                        MatchAction.MATCH_TO_END_DIALOG,
                        MatchAction.MATCH_ENDED,
                        MatchAction.MATCH_ENDED_DISCARD_FRAME_DIALOG
                    ) -> { // End match
                        matchViewModel.matchEnded(matchAction)
                        findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                    }
                    MatchAction.FOUL_DIALOG -> { // Navigate to foul dialog when queried
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

        // Menu items - tied to lifecycle owner; The lifecycle state indicates when the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_game_overflow, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.match_action_undo -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_UNDO, null, null, null)
                    R.id.match_action_add_red -> gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.ADDRED, null, null)
                    R.id.match_action_rerack -> gameViewModel.resetFrame()
                    R.id.match_action_cancel_match -> matchViewModel.cancelMatch()
                    R.id.match_action_concede_frame -> matchViewModel.assignEventMatchAction(MatchAction.FRAME_ENDED_DIALOG)
                    R.id.match_action_concede_match -> matchViewModel.assignEventMatchAction(MatchAction.MATCH_ENDED_DIALOG)
                    else -> return false
                }
                requireActivity().invalidateOptionsMenu() // Reset options menu every time a button is pressed
                return true
            }

            override fun onPrepareMenu(menu: Menu) { // Set the enabled value of menu items to match the match circumstances
                matchViewModel.displayFrame.value?.apply {
                    menu.findItem(R.id.match_action_undo).apply {
                        isEnabled = (frameStack.size) > 0 && !(gameViewModel.isUpdateInProgress.value ?: false)
                        setStateOpacity()
                    }
                    menu.findItem(R.id.match_action_rerack).apply {
                        isEnabled = (frameStack.size) > 0 && !(gameViewModel.isUpdateInProgress.value ?: false)
                    }
                    menu.findItem(R.id.match_action_add_red).apply {
                        isEnabled =
                            (ballStack.size) in (10..36).filter { it % 2 == 0 } && !(gameViewModel.isUpdateInProgress.value ?: false)
                    }
                    menu.findItem(R.id.match_action_concede_frame).apply {
                        isEnabled = getFrameScoreDiff() != 0 && !(gameViewModel.isUpdateInProgress.value ?: false)
                    }
                    menu.findItem(R.id.match_action_concede_match).apply {
                        isEnabled =
                            getFrameScoreDiff() != 0 || getMatchScoreDiff() != 0 && !(gameViewModel.isUpdateInProgress.value ?: false)
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // when pressing back, save match before killing fragment
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameFragmentScope.launch {
                    matchViewModel.autoSaveMatch()
                }
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
            }
        })

        return binding.root
    }
}
