package com.example.snookerscore.fragments.gamestatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.GenericViewModelFactory
import com.example.snookerscore.R
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.databinding.FragmentGameStatsBinding
import com.example.snookerscore.domain.DomainPlayerScore
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.domain.PlayerTagType
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.EventObserver
import kotlinx.android.synthetic.main.item_game_statistics_view.*
import timber.log.Timber

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
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameStatsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_stats, container, false)

        gameStatsViewModel.getTotals()

        // Listeners
        binding.apply {
            lifecycleOwner = this@GameStatsFragment
            varStatsViewModel = gameStatsViewModel
            varEventsViewModel = eventsViewModel
            application = requireActivity().application

            gameStatsRv.adapter = GameStatsAdapter()

            fragGameTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                playerTagType = PlayerTagType.STATISTICS
                application = requireActivity().application
            }

            gameStatsScrollView.viewTreeObserver.addOnGlobalLayoutListener {
                scrollHeight = gameStatsScrollView.measuredHeight
                gameStatsScrollView.assignScrollHeight()
            }
            gameStatsGhostFrameForHeight.viewTreeObserver.addOnGlobalLayoutListener {
                ghostHeight = gameStatsGhostFrameForHeight.measuredHeight
                gameStatsScrollView.assignScrollHeight()
            }

            // Header format
            gameStatsHeader.apply {
                varBgType = 2
                varTextType = 1
                frameScoreA = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
                frameScoreB = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
            }

            // Footer format
            gameStatsFooter.apply {
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
            eventsViewModel.apply {
                eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    if (matchAction == MatchAction.APP_TO_MAIN) findNavController().navigate(GameStatsFragmentDirections.actionGameStatsFragmentToPlayFragment())
                })
            }
        }

        return binding.root
    }

    private fun ScrollView.assignScrollHeight() {
        val params = layoutParams
        if (scrollHeight > ghostHeight) {
            params.height = ghostHeight
        }
    }
}