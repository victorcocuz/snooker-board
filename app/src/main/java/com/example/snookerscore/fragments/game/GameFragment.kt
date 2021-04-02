package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding
import timber.log.Timber

class GameFragment : androidx.fragment.app.Fragment() {

    private val viewModel: GameFragmentViewModel by activityViewModels()
    private lateinit var viewBalls: List<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        setHasOptionsMenu(true)

        binding.apply {
            lifecycleOwner = this@GameFragment
            gameViewModel = viewModel
            fragGameActions.gameViewModel = viewModel

            fragGameBalls.gameViewModel = viewModel
            fragGameBalls.apply {
                balls = Balls
                viewBalls = listOf(
                    gameBtnBallWhite,
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
        viewModel.frameState.observe(viewLifecycleOwner, Observer { frameState ->
            manageBallVisibility(frameState)
        })

        // Open foul dialog
        viewModel.foulCheck.observe(viewLifecycleOwner, Observer { foulCheck ->
            if (foulCheck) FoulDialogFragment().show(requireActivity().supportFragmentManager, "customDialog")
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    private fun manageBallVisibility(frameState: BallType) {

        when (frameState) {
            BallType.RED -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[1].visibility = View.VISIBLE
            }
            BallType.COLOR -> {
                viewBalls.forEach { e -> e.visibility = View.VISIBLE }
                viewBalls[0].visibility = View.GONE
                viewBalls[1].visibility = View.GONE
            }
            BallType.YELLOW -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[2].visibility = View.VISIBLE
            }
            BallType.GREEN -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[3].visibility = View.VISIBLE
            }
            BallType.BROWN -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[4].visibility = View.VISIBLE
            }
            BallType.BLUE -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[5].visibility = View.VISIBLE
            }
            BallType.PINK -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[6].visibility = View.VISIBLE
            }
            BallType.BLACK -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
                viewBalls[7].visibility = View.VISIBLE
            }
            BallType.END -> {
                viewBalls.forEach { e -> e.visibility = View.GONE }
            }
            else -> Timber.e("Logic not implemented for frame state $frameState")
        }
    }
}