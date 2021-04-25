package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.domain.Ball
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.domain.Pot
import com.example.snookerscore.utils.EventObserver
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var ballsList: List<Ball>
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

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
                        MatchAction.CANCEL_MATCH -> {
                            resetMatchScore()
                            requireActivity().onBackPressed()
                        }
                        in listOf(MatchAction.END_FRAME, MatchAction.FRAME_ENDED) -> frameEnded()
                        in listOf(MatchAction.END_MATCH, MatchAction.MATCH_ENDED) -> {
                            frameEnded()
                            findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
                        }
                        MatchAction.CONTINUE_MATCH -> {
                            eventsViewModel.eventMatchActionConfirmed(MatchAction.CONTINUE_MATCH)
                        }
                        MatchAction.START_NEW_MATCH -> eventsViewModel.eventMatchActionConfirmed(MatchAction.START_NEW_MATCH)
                        else -> {
                        }
                    }
                }
            })
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    private fun manageBallVisibility(frameState: Ball) {
        ballsList = when (frameState) {
            FREEBALL -> listOf(FREEBALL)
            RED -> listOf(RED)
            COLOR -> listOf(YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
            YELLOW -> listOf(YELLOW)
            GREEN -> listOf(GREEN)
            BROWN -> listOf(BROWN)
            BLUE -> listOf(BLUE)
            PINK -> listOf(PINK)
            BLACK -> listOf(BLACK)
            else -> listOf()
        }
        ballAdapter.submitList(ballsList)
    }
}
