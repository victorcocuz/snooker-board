package com.quickpoint.snookerboard.fragments.postgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.admob.AdMob
import com.quickpoint.snookerboard.databinding.FragmentPostGameBinding
import com.quickpoint.snookerboard.domain.DomainScore
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.NAV_TO_PLAY
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

class PostGameFragment : androidx.fragment.app.Fragment() {

    private val postGameVm: PostGameViewModel by lazy {
        ViewModelProvider(this, GenericViewModelFactory(this, null))[PostGameViewModel::class.java]
    }
    private val matchVm: MatchViewModel by activityViewModels()
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        postponeEnterTransition()
        matchVm.transitionToFragment(this, 200)

        // Bind view elements
        val binding: FragmentPostGameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_game, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varPostGameVm = postGameVm

            fragPostGameRv.apply {
                adapter = PostGameAdapter()
                itemAnimator = null
            }

            fragPostGameLayoutTop.apply {
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
                frameScoreA = DomainScore(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1)
                frameScoreB = DomainScore(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }

            // Footer format
            fragPostGameFooter.apply {
                varBgType = 2
                varTextType = 1
                postGameVm.totalsA.observe(viewLifecycleOwner) { frameScoreA = it }
                postGameVm.totalsB.observe(viewLifecycleOwner) { frameScoreB = it }
            }

            // VM Observers
            postGameVm.apply {
                eventPostGameAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    if (matchAction == NAV_TO_PLAY) {
                        matchVm.deleteMatchFromDb()
                        navigate(PostGameFragmentDirections.playFrag())
                    }
                })
            }
        }

        // Disable back pressing
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        return binding.root
    }
}