package com.quickpoint.snookerboard.fragments.gamestatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quickpoint.snookerboard.GenericEventsViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.databinding.FragmentGameStatsBinding
import com.quickpoint.snookerboard.domain.DomainPlayerScore
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import kotlinx.android.synthetic.main.item_game_statistics_view.*

class GameStatsFragment : Fragment() {

    private val gameStatsViewModel: GameStatsViewModel by lazy {
        ViewModelProvider(
            this, GenericViewModelFactory(
                requireNotNull(this.activity).application,
                SnookerRepository(SnookerDatabase.getDatabase(requireNotNull(this.activity).application)),
                this,
                null
            )
        ).get(GameStatsViewModel::class.java)
    }
    private val genericEventsViewModel: GenericEventsViewModel by activityViewModels()
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameStatsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_stats, container, false)

        gameStatsViewModel.getTotals() // Gets the score from repository and stores it in live data within the vm

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = this@GameStatsFragment
            varStatsViewModel = gameStatsViewModel
            varEventsViewModel = genericEventsViewModel
            varApplication = requireActivity().application

            fragStatsRv.adapter = GameStatsAdapter()

            fragStatsLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varPlayerTagType = PlayerTagType.STATISTICS
                varApplication = requireActivity().application
            }

            fragStatsScrollView.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
                scrollHeight = fragStatsScrollView.measuredHeight
                fragStatsScrollView.assignScrollHeight(scrollHeight, ghostHeight)
            }
            fragStatsGhostFrameForHeight.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
                ghostHeight = fragStatsGhostFrameForHeight.measuredHeight
                fragStatsScrollView.assignScrollHeight(scrollHeight, ghostHeight)
            }

            // Header format
            fragStatsHeader.apply {
                varBgType = 2
                varTextType = 1
                frameScoreA = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
                frameScoreB = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
            }

            // Footer format
            fragStatsFooter.apply {
                varBgType = 2
                varTextType = 1
                gameStatsViewModel.totalsA.observe(viewLifecycleOwner, {
                    frameScoreA = it
                })
                gameStatsViewModel.totalsB.observe(viewLifecycleOwner, {
                    frameScoreB = it
                })
            }

            // VM Observers
            genericEventsViewModel.apply {
                eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    if (matchAction == MatchAction.APP_TO_MAIN) findNavController().navigate(GameStatsFragmentDirections.actionGameStatsFragmentToPlayFragment())
                })
            }
        }

        return binding.root
    }
}