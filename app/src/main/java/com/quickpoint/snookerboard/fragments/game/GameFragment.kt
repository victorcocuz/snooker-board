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
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import kotlinx.android.synthetic.main.fragment_game_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))

        ballAdapter = BallAdapter(
            BallListener { ball ->
                requireActivity().invalidateOptionsMenu()
                gameViewModel.handleFrameEvent(FrameEvent.HANDLE_POT, DomainPot.HIT(ball), null, null)
            },
            gameViewModel.displayFrame,
            BallAdapterType.MATCH
        )

        binding.apply {
            lifecycleOwner = this@GameFragment
            setHasOptionsMenu(true)

            application = requireActivity().application
            varGameViewModel = this@GameFragment.gameViewModel
            fragGameTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                playerTagType = PlayerTagType.MATCH
                varGameViewModel = gameViewModel
                application = requireActivity().application
            }

            fragGameScore.apply {
                varGameViewModel = this@GameFragment.gameViewModel
                application = requireActivity().application
            }

            fragGameGhostLayout.viewTreeObserver.addOnGlobalLayoutListener {
                ghostHeight = fragGameGhostLayout.measuredHeight
                fragGameScrollView.assignScrollHeight(scrollHeight, ghostHeight)
            }

            fragGameScrollView.viewTreeObserver.addOnGlobalLayoutListener {
                scrollHeight = fragGameScrollView.measuredHeight
                fragGameScrollView.assignScrollHeight(scrollHeight, ghostHeight)
                fragGameScrollView.scrollToBottom()
            }

            fragGameBreakRv.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
                (layoutManager as LinearLayoutManager).stackFromEnd = true
            }

            fragGameActionButtons.apply {
                gameViewModel = this@GameFragment.gameViewModel
                genericEventsViewModel = eventsViewModel
                Timber.e("wtf ${requireContext().getFactoredDimen(BALL_HEIGHT_FACTOR_MATCH_ACTION)}")
                Timber.e("wtf ${resources.getDimension(R.dimen.margin_layout_offset) * 2}")
                fragGameBallsLl.layoutParams.height = requireContext().getFactoredDimen(BALL_HEIGHT_FACTOR_MATCH_ACTION) + resources.getDimension(R.dimen.margin_layout_offset).toInt() * 2
                fragGameBallsRv.apply {
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = null
                    adapter = ballAdapter
                }
            }
        }

        // VM Observers
        gameViewModel.apply {
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                findNavController().navigate(
                    GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                        MatchAction.NO_ACTION,
                        if (matchAction == MatchAction.MATCH_END_QUERY) MatchAction.MATCH_END_CONFIRM_DISCARD else MatchAction.NO_ACTION,
                        matchAction
                    )
                )
            })
            eventFrameUpdated.observe(viewLifecycleOwner, EventObserver {
                requireActivity().invalidateOptionsMenu()
            })
        }
        eventsViewModel.apply {
            eventFoulQueried.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
            })
            eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                gameViewModel.apply {
                    when (matchAction) {
                        MatchAction.FOUL_CONFIRMED -> {
                            handleFrameEvent(
                                FrameEvent.HANDLE_FOUL,
                                getFoul(),
                                eventsViewModel.isRemoveRed.value!!,
                                eventsViewModel.isFreeBall.value!!
                            )
                            eventsViewModel.resetFoul()
                        }
                        MatchAction.MATCH_CANCEL -> {
                            cancelMatch()
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
                        }
                        in listOf(MatchAction.FRAME_END_QUERY, MatchAction.FRAME_END_CONFIRM) -> {
                            frameEnded()
                        }
                        in listOf(MatchAction.MATCH_END_QUERY, MatchAction.MATCH_END_CONFIRM, MatchAction.MATCH_END_CONFIRM_DISCARD) -> {
                            matchEnded(matchAction)
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                        }
                        MatchAction.MATCH_RELOAD -> {
                            eventsViewModel.onEventMatchActionConfirmed(MatchAction.MATCH_RELOAD)
                        }
                        MatchAction.MATCH_START_NEW -> eventsViewModel.onEventMatchActionConfirmed(MatchAction.MATCH_START_NEW)
                        else -> {
                        }
                    }
                }
            })
        }

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
            R.id.match_action_cancel_match -> gameViewModel.assignMatchAction(MatchAction.MATCH_CANCEL)
            R.id.match_action_concede_match -> gameViewModel.queryEndMatch()
        }
        requireActivity().invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) { // Buttons cannot be enabled when update is in progress
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
