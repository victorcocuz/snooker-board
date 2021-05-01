package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.domain.BallAdapterType
import com.example.snookerscore.domain.DomainBall
import com.example.snookerscore.domain.DomainBall.*
import com.example.snookerscore.domain.DomainPot
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.EventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var ballsList: List<DomainBall>
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))

        ballAdapter = BallAdapter(
            BallListener { ball ->
                requireActivity().invalidateOptionsMenu()
                gameViewModel.updateFrame(DomainPot.HIT(ball))
            },
            gameViewModel.displayFrame,
            BallAdapterType.MATCH
        )

        binding.apply {
            lifecycleOwner = this@GameFragment
            (activity as AppCompatActivity).apply {
                setSupportActionBar(fragGameToolbar)
                supportActionBar?.setDisplayShowTitleEnabled(false)
            }
            setHasOptionsMenu(true)

            gameViewModel = this@GameFragment.gameViewModel
            fragGameBallsRv.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
                adapter = ballAdapter
            }
            fragGameBreakRv.apply {
                adapter = BreakAdapter(requireActivity())
                itemAnimator = null
            }
            fragGameScore.apply {
                gameViewModel = this@GameFragment.gameViewModel
            }
            fragGameActionButtons.apply {
                gameViewModel = this@GameFragment.gameViewModel
                genericEventsViewModel = eventsViewModel
            }
        }

        // VM Observers
        gameViewModel.apply {
            // Enable or disable buttons
            displayFrame.observe(viewLifecycleOwner, { frame ->
                manageBallVisibility(frame.ballStack.lastOrNull())
            })
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                findNavController().navigate(
                    GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(
                        matchAction,
                        MatchAction.NO_ACTION
                    )
                )
            })
        }
        eventsViewModel.apply {
            eventFoul.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
            })
            eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                gameViewModel.apply {
                    when (matchAction) {
                        MatchAction.MATCH_CANCEL -> {
                            gameFragmentScope.launch {
                                snookerRepository.deleteMatchFrames()
                                snookerRepository.deleteCurrentMatch()
                            }
                            startNewMatch()
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToPlayFragment())
                        }
                        in listOf(MatchAction.FRAME_END_QUERY, MatchAction.FRAME_END_CONFIRM) -> {
                            ballAdapter.submitList(null)
                            frameEnded()
                        }
                        in listOf(MatchAction.MATCH_END_QUERY, MatchAction.MATCH_END_CONFIRM) -> {
                            frameEnded()
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                        }
                        MatchAction.MATCH_CONTINUE -> {
                            eventsViewModel.onEventMatchActionConfirmed(MatchAction.MATCH_CONTINUE)
                        }
                        MatchAction.MATCH_START_NEW -> eventsViewModel.onEventMatchActionConfirmed(MatchAction.MATCH_START_NEW)
                        else -> {
                        }
                    }
                }
                requireActivity().invalidateOptionsMenu()
            })
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameFragmentScope.launch {
                    gameViewModel.saveCurrentMatch()
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
            R.id.match_action_undo -> gameViewModel.undo()
            R.id.match_action_add_red -> gameViewModel.updateFrame(DomainPot.ADDRED)
            R.id.match_action_rerack -> gameViewModel.startNewFrame()
            R.id.match_action_concede_frame -> gameViewModel.endFrame()
            R.id.match_action_cancel_match -> gameViewModel.assignMatchAction(MatchAction.MATCH_CANCEL)
            R.id.match_action_concede_match -> gameViewModel.assignMatchAction(MatchAction.MATCH_END_QUERY)
        }
        requireActivity().invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        gameViewModel.apply {
            menu.findItem(R.id.match_action_undo).isEnabled = (displayFrame.value?.frameStack?.size ?: 0) > 0
            menu.findItem(R.id.match_action_rerack).isEnabled = (displayFrame.value?.frameStack?.size ?: 0) > 0
            menu.findItem(R.id.match_action_add_red).isEnabled =
                (displayFrame.value?.frameStack?.size ?: 0) in (10..36).filter { it % 2 == 0 }
            menu.findItem(R.id.match_action_concede_frame).isEnabled =
                displayFrame.value?.frameScore?.get(0)?.framePoints != displayFrame.value?.frameScore?.get(1)?.framePoints
            menu.findItem(R.id.match_action_concede_match).isEnabled =
                displayFrame.value?.frameScore?.get(0)?.framePoints != displayFrame.value?.frameScore?.get(1)?.framePoints
                        || displayFrame.value?.frameScore?.get(0)?.matchPoints != displayFrame.value?.frameScore?.get(1)?.matchPoints
        }
    }

    private fun manageBallVisibility(frameState: DomainBall?) {
        ballsList = when (frameState) {
            is FREEBALL -> listOf(FREEBALL())
            is RED -> listOf(RED())
            is COLOR -> listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
            is YELLOW -> listOf(YELLOW())
            is GREEN -> listOf(GREEN())
            is BROWN -> listOf(BROWN())
            is BLUE -> listOf(BLUE())
            is PINK -> listOf(PINK())
            is BLACK -> listOf(BLACK())
            else -> listOf()
        }
        ballAdapter.submitList(ballsList)
    }
}
