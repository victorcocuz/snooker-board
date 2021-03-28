package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameBinding

class GameFragment : androidx.fragment.app.Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        binding.fragGameButton.setOnClickListener {
            it.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameStatsFragment())
        }

        binding.gameViewModel = viewModel
        binding.fragGameButtons.gameViewModel = viewModel
        binding.lifecycleOwner = this

//        viewModel.scorePlayerA.observe(viewLifecycleOwner, Observer {
//            binding.gameTextScoreA.text = it.toString()
//        })
//
//        viewModel.eventFrameComplete.observe(viewLifecycleOwner, Observer {
//            if (it == true) {
//                binding.gameTextScoreFramesA.text = it.toString()
//                viewModel.resetFrame()
//            }
//        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_game_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}