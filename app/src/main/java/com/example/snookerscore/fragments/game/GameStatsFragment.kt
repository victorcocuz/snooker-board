package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameStatsBinding
import timber.log.Timber

class GameStatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameStatsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_stats, container, false)
        val args = GameStatsFragmentArgs.fromBundle(requireArguments()).match
        Timber.e("args $args")

        // Listeners
        binding.fragGameStatsBtn.setOnClickListener {
            it.findNavController().navigate(GameStatsFragmentDirections.actionGameStatsFragmentToPlayFragment())
        }
        return binding.root
    }
}