package com.quickpoint.snookerboard.fragments.rules

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentRulesBinding
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.*
import timber.log.Timber

class RulesFragment : Fragment() {
    // Variables
    private val matchVm: MatchViewModel by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        matchVm.matchState.observeOnce(viewLifecycleOwner) { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                IDLE -> {
                    rulesVm.updateRules(SETTINGS.resetRules())
                    matchVm.transitionToFragment(this, 0)
                }
                IN_PROGRESS, POST_MATCH -> {
                    startPostponedEnterTransition()
                    view?.visibility = GONE
                    navigate(if (matchState == IN_PROGRESS) RulesFragmentDirections.gameFrag() else RulesFragmentDirections.summaryFrag())
                }
                else -> Timber.e("matchState should not be $matchState at this point")
            }
        }

        // Bind view elements
        val binding: FragmentRulesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varRulesVm = rulesVm

            // Bind fragment rules elements
            fRulesLMain.apply {
                varMatchVm = matchVm
                varRulesVm = rulesVm
                lRulesMainNpFrameCount.apply { // For displayedValues get an array of odd numbers for the number of frames
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }

                // VM Observers
                rulesVm.eventRulesAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    when (matchAction) { // Start new match
                        INFO_FOUL_DIALOG -> navigate(RulesFragmentDirections.genDialogFrag(IGNORE, IGNORE, matchAction))
                        MATCH_PLAY -> {
                            Timber.i(SETTINGS.getAsText())
                            navigate(RulesFragmentDirections.gameFrag())
                        }
                        SNACKBAR_NO_PLAYER -> fRulesCdl.snackbar(getString(R.string.snackbar_f_rules_no_name))
                        SNACKBAR_NO_FIRST -> fRulesCdl.snackbar(getString(R.string.snackbar_f_rules_select_who_breaks))
                        else -> Timber.i("Implementation for observed matchAction $matchAction not supported")                    }
                })
            }
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