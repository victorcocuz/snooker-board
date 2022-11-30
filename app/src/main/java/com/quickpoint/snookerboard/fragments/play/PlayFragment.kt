package com.quickpoint.snookerboard.fragments.play

import android.os.Bundle
import android.view.*
import android.view.View.*
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
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.MatchRules.RULES
import com.quickpoint.snookerboard.utils.MatchState.*
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
        postponeEnterTransition() // Wait for data to load before displaying fragment
      Timber.e("start play fragment")
        matchVm.matchState.observeOnce(viewLifecycleOwner) { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                IDLE -> {
                    playVm.updateRules(RULES.resetRules())
                    matchVm.transitionToFragment(this, 0)
                }
                IN_PROGRESS, POST_MATCH -> {
                    startPostponedEnterTransition()
                    view?.visibility = GONE
                    navigate(if (matchState == IN_PROGRESS) PlayFragmentDirections.gameFrag() else PlayFragmentDirections.postGameFrag())
                }
                else -> Timber.e("matchState should not be $matchState at this point")
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
                varPlayVm = playVm
                numberPicker.apply { // For displayedValues get an array of odd numbers for the number of frames
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }

                // VM Observers
                playVm.eventPlayAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    when (matchAction) { // Start new match
                        SNACKBAR_NO_PLAYER -> fragPlayCoordLayout.snackbar(getString(R.string.snackbar_f_play_no_name))
                        SNACKBAR_NO_FIRST -> fragPlayCoordLayout.snackbar(getString(R.string.snackbar_f_play_select_who_breaks))
                        MATCH_PLAY -> navigate(PlayFragmentDirections.gameFrag())
                        INFO_FOUL_DIALOG -> navigate(PlayFragmentDirections.genDialogFrag(IGNORE, IGNORE, matchAction))
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

        // Disable back pressing
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

        return binding.root
    }
}