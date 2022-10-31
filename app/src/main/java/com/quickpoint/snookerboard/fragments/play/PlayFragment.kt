package com.quickpoint.snookerboard.fragments.play

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentPlayBinding
import com.quickpoint.snookerboard.domain.MatchState.IN_PROGRESS
import com.quickpoint.snookerboard.domain.MatchState.POST_MATCH
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.hideKeyboard
import com.quickpoint.snookerboard.utils.navigate
import timber.log.Timber

class PlayFragment : androidx.fragment.app.Fragment() {
    // Variables
    private val matchVm: MatchViewModel by activityViewModels()
    private val playVm: PlayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        matchVm.eventRules.observe(viewLifecycleOwner) { rules ->
            when (rules.matchState) {
                IN_PROGRESS -> navigate(PlayFragmentDirections.gameFrag())
                POST_MATCH -> navigate(PlayFragmentDirections.postGameFrag())
                else -> {}
            }
        }

        // Bind view elements
        val binding: FragmentPlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varPlayVm = playVm

            // Bind fragment rules elements
            fragPlayRules.apply {
                varMatchVm = matchVm
                numberPicker.apply { // For displayedValues get an array of odd numbers for the number of frames
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }

                // VM Observers
                playVm.eventPlayAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    when (matchAction) { // Start new match
                        MATCH_PLAY -> navigate(PlayFragmentDirections.gameFrag())
                        else -> {}
                    }
                })
                matchVm.eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    when (matchAction) {
                        INFO_FOUL_DIALOG -> navigate(PlayFragmentDirections.genDialogFrag(IGNORE, IGNORE, matchAction))
                        else -> Timber.i("Implementation for observed matchAction $matchAction not supported")
                    }
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

        // Disable back pressing
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

        return binding.root
    }
}