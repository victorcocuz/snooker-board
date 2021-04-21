package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.fragments.game.Ball.*
import com.example.snookerscore.utils.EventObserver
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels()
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
            gameFragmentViewModel.updateFrame(Pot.HIT(ball))
        }, gameFragmentViewModel.displayBallStack)

        binding.apply {
            lifecycleOwner = this@GameFragment
            gameViewModel = gameFragmentViewModel
            fragGameBallsRv.apply {
                layoutManager = linearLayoutManager
                itemAnimator = null
                adapter = ballAdapter
            }
            fragGameActions.apply {
                gameViewModel = gameFragmentViewModel
            }
        }

        // VM Observers
        gameFragmentViewModel.apply {
            // Enable or disable buttons
            displayBallStack.observe(viewLifecycleOwner, { ballStack ->
                manageBallVisibility(ballStack.peek()!!)
            })

            // Open foul dialog
            eventFoul.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFoulDialogFragment())
            })
            eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameGenericDialogFragment(matchAction))
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
