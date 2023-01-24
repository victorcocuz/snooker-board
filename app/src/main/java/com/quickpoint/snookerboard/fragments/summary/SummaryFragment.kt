package com.quickpoint.snookerboard.fragments.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

//    private val summaryVm: SummaryViewModel by lazy {
//        ViewModelProvider(this, GenericViewModelFactory(this, null))[SummaryViewModel::class.java]
//    }
    private val mainVm: MainViewModel by activityViewModels()
    private var scrollHeight = 0
    private var ghostHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        postponeEnterTransition()
        mainVm.turnOffSplashScreen(200)

        // Bind view elements
        val binding: FragmentSummaryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary, container, false)
//        binding.apply {
//            lifecycleOwner = viewLifecycleOwner
//            varSummaryVm = summaryVm
//
//            fSummaryRvScore.apply {
//                adapter = SummaryAdapter()
//                itemAnimator = null
//            }
//
//            fSummaryLTop.apply {
//                varPlayerTagType = PlayerTagType.STATISTICS
//            }
//
//            fSummarySvScore.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
//                scrollHeight = fSummarySvScore.measuredHeight
//                fSummarySvScore.assignScrollHeight(scrollHeight, ghostHeight)
//            }
//            fSummaryFlGhostFrameForHeight.viewTreeObserver.addOnGlobalLayoutListener { // Assign ghost & scroll view height that lines up with the top of the action button
//                ghostHeight = fSummaryFlGhostFrameForHeight.measuredHeight
//                fSummarySvScore.assignScrollHeight(scrollHeight, ghostHeight)
//            }
//
//            // Header format
//            fSummaryLStatsHeader.apply {
//                varBgType = 2
//                varTextType = 1
//                frameScoreA = DomainScore(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1)
//                frameScoreB = DomainScore(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
//            }
//
//            // Footer format
//            fSummaryLStatsFooter.apply {
//                varBgType = 2
//                varTextType = 1
//                summaryVm.totalsA.observe(viewLifecycleOwner) { frameScoreA = it }
//                summaryVm.totalsB.observe(viewLifecycleOwner) { frameScoreB = it }
//            }
//
//            // VM Observers
//            summaryVm.apply {
//                eventSummaryAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
//                    if (matchAction == NAV_TO_PLAY) {
//                        mainVm.deleteMatchFromDb()
//                        navigate(SummaryFragmentDirections.rulesFrag())
//                    }
//                })
//            }
//        }

        // Handle back button pressing
//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                summaryVm.onEventSummaryAction(NAV_TO_PLAY)
//            }
//        })
//
        return binding.root
    }
}

@Composable
fun FragmentSummary(
    navController: NavController,
    mainVm: MainViewModel
) {
    Text("summary fragment")
}