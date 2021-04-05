package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.fragments.game.dialog.FoulDialogFragment
import com.example.snookerscore.utils.EventObserver
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private lateinit var ballsList: List<Ball>
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels {
        GameFragmentViewModelFactory(
            requireNotNull(this.activity).application
        )
    }
    private lateinit var ballAdapter: BallAdapter
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        setHasOptionsMenu(true)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.apply {
            canScrollHorizontally()

        }
        ballAdapter = BallAdapter(BallListener { ball ->
            gameFragmentViewModel.onBallClicked(Pot(ball, PotType.HIT, ShotActions.CONTINUE))
        })

        binding.apply {
            lifecycleOwner = this@GameFragment
            gameViewModel = gameFragmentViewModel
            fragGameBallsRv.apply {
                layoutManager = linearLayoutManager
                itemAnimator = null
                adapter = ballAdapter
            }
            fragGameActions.gameViewModel = gameFragmentViewModel

            fragGameButton.setOnClickListener {
                it.findNavController()
                    .navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
            }
        }

        // VM Observers
        gameFragmentViewModel.apply {
            // Enable or disable buttons
            frameState.observe(viewLifecycleOwner, { frameState ->
                manageBallVisibility(frameState)
            })

            // Open foul dialog
            eventFoul.observe(viewLifecycleOwner, EventObserver {
                FoulDialogFragment().show(requireActivity().supportFragmentManager, "customDialog")
            })
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    private fun manageBallVisibility(frameState: BallType) {
        Balls.apply {
            ballsList = when (frameState) {
                BallType.RED -> listOf(RED)
                BallType.COLOR -> listOf(YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
                BallType.YELLOW -> listOf(YELLOW)
                BallType.GREEN -> listOf(GREEN)
                BallType.BROWN -> listOf(BROWN)
                BallType.BLUE -> listOf(BLUE)
                BallType.PINK -> listOf(PINK)
                BallType.BLACK -> listOf(BLACK)
                else -> listOf()
            }
            ballAdapter.submitList(ballsList)
        }
    }
}
