package com.quickpoint.snookerboard.fragments.postgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentPostGameBinding
import com.quickpoint.snookerboard.domain.DomainMatchInfo.RULES
import com.quickpoint.snookerboard.domain.DomainPlayerScore
import com.quickpoint.snookerboard.domain.MatchState.IDLE
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.MatchAction.NAVIGATE_HOME
import com.quickpoint.snookerboard.utils.PlayerTagType
import com.quickpoint.snookerboard.utils.assignScrollHeight

class PostGameFragment : androidx.fragment.app.Fragment() {

    private val postGameVm: PostGameViewModel by lazy {
        ViewModelProvider(
            this, GenericViewModelFactory(
                requireNotNull(this.activity).application,
                this,
                null
            )
        )[PostGameViewModel::class.java]
    }
    private val matchVm: MatchViewModel by activityViewModels()
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPostGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_game, container, false)

        postGameVm.getTotals() // Gets the score from repository and stores it in live data within the vm

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varPostGameVm = postGameVm
            varMatchVm = this@PostGameFragment.matchVm

            fragPostGameRv.adapter = PostGameAdapter()

            fragPostGameLayoutTop.apply {
                (activity as AppCompatActivity).apply {
                    setSupportActionBar(fragGameToolbar)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                varPlayerTagType = PlayerTagType.STATISTICS
            }

            fragPostGameScrollView.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
                scrollHeight = fragPostGameScrollView.measuredHeight
                fragPostGameScrollView.assignScrollHeight(scrollHeight, ghostHeight)
            }
            fragPostGameGhostFrameForHeight.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
                ghostHeight = fragPostGameGhostFrameForHeight.measuredHeight
                fragPostGameScrollView.assignScrollHeight(scrollHeight, ghostHeight)
            }

            // Header format
            fragPostGameHeader.apply {
                varBgType = 2
                varTextType = 1
                frameScoreA = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
                frameScoreB = DomainPlayerScore(-1, -1, -1, -1, -1, 0, -1, -1)
            }

            // Footer format
            fragPostGameFooter.apply {
                varBgType = 2
                varTextType = 1
                postGameVm.totalsA.observe(viewLifecycleOwner) { frameScoreA = it }
                postGameVm.totalsB.observe(viewLifecycleOwner) { frameScoreB = it }
            }

            // VM Observers
            matchVm.apply {
                eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    if (matchAction == NAVIGATE_HOME) {
                        RULES.state = IDLE
                        deleteMatchFromDb()
                        findNavController().navigate(PostGameFragmentDirections.actionGameStatsFragmentToPlayFragment())
                    }
                })
            }
        }

        return binding.root
    }
}