package com.example.snookerscore.fragments.gamestatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.snookerscore.GenericViewModelFactory
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameStatsBinding
import com.example.snookerscore.fragments.game.Frame
import com.example.snookerscore.utils.setupGameNotification
import kotlinx.android.synthetic.main.item_game_statistics_view.*

class GameStatsFragment : Fragment() {


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
            gameStatsHeader.apply {
                itemGamestatsLinearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_secondary))
                frame = Frame(0, listOf())

            }
            gameStatsFooter.apply {
                itemGamestatsLinearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_secondary))
//                frame = Frame(100, viewModel.frames)
            }
            gameStatsHeader.apply {
//                itemGamestatsFrameNumber.text = "#"
//                itemGamestatsPlayerABreak.text = getString(R.string.fragment_statistics_header_break)
//                itemGamestatsPlayerBBreak.text = getString(R.string.fragment_statistics_header_break)
//                itemGamestatsPlayerAPercentage.text = "%"
//                itemGamestatsPlayerBPercentage.text = "%"
//                itemGamestatsPlayerAFramePoints.text = getString(R.string.fragment_statistics_header_points)
//                itemGamestatsPlayerBFramePoints.text = getString(R.string.fragment_statistics_header_points)
//                itemGamestatsMatchPoints.text = getString(R.string.fragment_statistics_header_score)
            }
        }

        setupGameNotification(requireActivity())

        return binding.root
    }
}