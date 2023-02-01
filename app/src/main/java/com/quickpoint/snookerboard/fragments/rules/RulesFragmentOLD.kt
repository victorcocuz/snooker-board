package com.quickpoint.snookerboard.fragments.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.quickpoint.snookerboard.MainViewModelOld
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.hideKeyboard
import com.quickpoint.snookerboard.utils.navigate
import timber.log.Timber

class RulesFragmentOld : Fragment() {
    // Variables
    private val mainVm: MainViewModelOld by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        if (Settings.matchState == RULES_IDLE) mainVm.transitionToFragment(this, 0) // For returning from navDrawer
        mainVm.matchState.observe(viewLifecycleOwner, EventObserver { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                RULES_IDLE -> {
                    mainVm.transitionToFragment(this, 0)
                }

                GAME_IN_PROGRESS, SUMMARY -> {
                    startPostponedEnterTransition()
                    view?.visibility = GONE
                    navigate(if (matchState == GAME_IN_PROGRESS) RulesFragmentDirections.gameFrag() else RulesFragmentDirections.summaryFrag())
                }

                else -> Timber.e("No implementation for state $matchState at this point")
            }
        })

        return ComposeView(requireContext()).apply {
            setContent {}
        }
    }
}
