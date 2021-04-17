package com.example.snookerscore.fragments.gamestatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.snookerscore.GenericViewModelFactory
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameStatsBinding
import com.example.snookerscore.utils.setupGameNotification

class GameStatsFragment : Fragment() {

//    private val application = requireNotNull(this.activity).application

    private val gameStatsViewModel: GameStatsViewModel by lazy {
        ViewModelProvider(this, GenericViewModelFactory(requireNotNull(this.activity).application)).get(GameStatsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameStatsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_stats, container, false)

        // Listeners
        binding.apply {
            gameStatsBtn.setOnClickListener {
                it.findNavController().navigate(GameStatsFragmentDirections.actionGameStatsFragmentToPlayFragment())
            }
            lifecycleOwner = this@GameStatsFragment
            viewModel = gameStatsViewModel
            gameStatsRv.adapter = GameStatsAdapter()
        }

        setupGameNotification(requireActivity())

        return binding.root
    }
}