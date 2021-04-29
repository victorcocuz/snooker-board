package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.domain.Ball
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.domain.Pot
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.EventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var ballsList: List<Ball>
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding
    private val gameFragmentScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.apply {
            canScrollHorizontally()

        }
        ballAdapter = BallAdapter(BallListener { ball ->
            gameViewModel.updateFrame(Pot.HIT(ball))
        }, gameViewModel.displayBallStack)

        binding.apply {
            lifecycleOwner = this@GameFragment
            gameViewModel = this@GameFragment.gameViewModel
            fragGameBallsRv.apply {
                layoutManager = linearLayoutManager
                itemAnimator = null
                adapter = ballAdapter
            }
            fragGameBreakRv.adapter = BreakAdapter(requireActivity())
            fragGameActionBtns.apply {
                gameViewModel = this@GameFragment.gameViewModel
                genericEventsViewModel = eventsViewModel
            }
        }

        // VM Observers
        gameViewModel.apply {
            // Enable or disable buttons
            displayBallStack.observe(viewLifecycleOwner, { ballStack ->
                manageBallVisibility(ballStack.last())
            })
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(matchAction, MatchAction.NO_ACTION))
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
                            resetMatch()
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
            })
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gameViewModel.assignMatchAction(MatchAction.MATCH_CANCEL)
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    private fun manageBallVisibility(frameState: Ball) {
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
