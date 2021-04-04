package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding
import com.example.snookerscore.fragments.game.ShotType.HIT
import com.example.snookerscore.fragments.game.dialog.FoulDialogFragment
import com.example.snookerscore.utils.EventObserver
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private lateinit var ballsList: List<Pair<Ball, ShotType>>
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
        ballAdapter = BallAdapter(BallListener { ball, shotType ->
            gameFragmentViewModel.onBallClicked(ball, shotType)
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
                BallType.RED -> listOf(Pair(RED, HIT))
                BallType.COLOR -> listOf(Pair(YELLOW, HIT) , Pair(GREEN, HIT), Pair(BROWN, HIT), Pair(BLUE, HIT), Pair(PINK, HIT), Pair(BLACK, HIT))
                BallType.YELLOW -> listOf(Pair(YELLOW, HIT))
                BallType.GREEN -> listOf(Pair(GREEN, HIT))
                BallType.BROWN -> listOf(Pair(BROWN, HIT))
                BallType.BLUE -> listOf(Pair(BLUE, HIT))
                BallType.PINK -> listOf(Pair(PINK, HIT))
                BallType.BLACK -> listOf(Pair(BLACK, HIT))
                else -> listOf()
            }
            ballAdapter.submitList(ballsList)
        }
    }
}
