package com.quickpoint.snookerboard.fragments.rules

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentRulesBinding
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.*
import com.quickpoint.snookerboard.utils.hideKeyboard
import com.quickpoint.snookerboard.utils.navigate
import com.quickpoint.snookerboard.utils.snackbar
import timber.log.Timber

class RulesFragment : Fragment() {
    // Variables
    private val mainVm: MainViewModel by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        if (SETTINGS.matchState == RULES_IDLE) mainVm.transitionToFragment(this, 0) // For returning from navDrawer
        mainVm.matchState.observe(viewLifecycleOwner, EventObserver { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                RULES_IDLE -> {
                    rulesVm.updateRules()
                    rulesVm.updatePlayer()
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

        // Bind view elements
        val binding: FragmentRulesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varRulesVm = rulesVm

            // Bind fragment rules elements
            fRulesLMain.apply {
                varRulesVm = rulesVm
                lRulesMainNpFrameCount.apply { // For displayedValues get an array of odd numbers for the number of frames
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }
            }
            fRulesLExtra.apply {
                varRulesVm = rulesVm
            }
        }

        // VM Observers
        rulesVm.eventRulesAction.observe(viewLifecycleOwner, EventObserver { action ->
            when (action) { // Start new match
                INFO_FOUL_DIALOG -> navigate(RulesFragmentDirections.genDialogFrag(IGNORE, IGNORE, action))
                MATCH_PLAY -> {
                    Timber.i(SETTINGS.getAsText())
                    navigate(RulesFragmentDirections.gameFrag())
                }
                SNACK_NO_PLAYER, SNACK_NO_FIRST, SNACK_HANDICAP_FRAME_LIMIT, SNACK_HANDICAP_MATCH_LIMIT -> binding.snackbar(action)
                else -> Timber.i("Implementation for observed action $action not supported")
            }
        })

        mainVm.matchToggle.observe(viewLifecycleOwner) { toggle ->
            binding.fRulesLExtra.root.visibility = if (toggle.toggleAdvancedRulesOn()) VISIBLE else GONE
        }

        // Add toolbar menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean { // Navbar navigation listener
                return view?.findNavController()?.let { NavigationUI.onNavDestinationSelected(menuItem, it) } ?: false
            }
        })

        return binding.root
    }
}