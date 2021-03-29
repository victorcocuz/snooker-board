package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding

class GameFragment : androidx.fragment.app.Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var balls: List<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        binding.apply {
            lifecycleOwner = this@GameFragment
            gameViewModel = viewModel
            fragGameActions.gameViewModel = viewModel

            fragGameBalls.gameViewModel = viewModel
            fragGameBalls.apply {
                balls = listOf(
                    gameBtnBallRed,
                    gameBtnBallYellow,
                    gameBtnBallGreen,
                    gameBtnBallBrown,
                    gameBtnBallBlue,
                    gameBtnBallPink,
                    gameBtnBallBlack
                )
            }

            fragGameButton.setOnClickListener {
                it.findNavController()
                    .navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
            }
        }

        // Enable or disable buttons
//        manageBallVisibility(FrameState.RED)
        viewModel.frameState.observe(viewLifecycleOwner, Observer { frameState ->
            manageBallVisibility(frameState)
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    private fun manageBallVisibility(frameState: FrameState) {
        when (frameState) {
            FrameState.RED -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[0].visibility = View.VISIBLE
            }
            FrameState.COLOR -> {
                balls.forEach { e -> e.visibility = View.VISIBLE }
                balls[0].visibility = View.GONE
            }
            FrameState.YELLOW -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[1].visibility = View.VISIBLE
            }
            FrameState.GREEN -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[2].visibility = View.VISIBLE
            }
            FrameState.BROWN -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[3].visibility = View.VISIBLE
            }
            FrameState.BLUE -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[4].visibility = View.VISIBLE
            }
            FrameState.PINK -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[5].visibility = View.VISIBLE
            }
            FrameState.BLACK -> {
                balls.forEach { e -> e.visibility = View.GONE }
                balls[6].visibility = View.VISIBLE
            }
            FrameState.END -> {
                balls.forEach { e -> e.visibility = View.GONE }
            }
        }
    }
}