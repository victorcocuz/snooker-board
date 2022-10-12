package com.quickpoint.snookerboard.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentPlayBinding
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

class PlayFragment : androidx.fragment.app.Fragment() {
    // Variables
    private val matchVm: MatchViewModel by activityViewModels()
    private val playVm: PlayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Bind layout, initial setup
        val binding: FragmentPlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        hideKeyboard()

        // Bind layout elements
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
                        MATCH_START -> findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                        else -> {}
                    }
                })
                matchVm.eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    when (matchAction) {
                        INFO_FOUL_DIALOG -> { // Open the foul info dialog
                            findNavController().navigate(
                                PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                    IGNORE, IGNORE,
                                    INFO_FOUL_DIALOG
                                )
                            )
                        }
                        else -> Timber.i("Implementation for observed matchAction $matchAction not supported")
                    }
                })
            }
        }
        return binding.root
    }
}