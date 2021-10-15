package com.quickpoint.snookerboard.fragments.play

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quickpoint.snookerboard.GenericEventsViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentPlayBinding
import com.quickpoint.snookerboard.utils.*

class PlayFragment : androidx.fragment.app.Fragment() {
    // Variables
    private val genericEventsViewModel: GenericEventsViewModel by activityViewModels()
    private val playViewModel: PlayFragmentViewModel by viewModels()
    private val matchViewModel: MatchViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Bind layout, initial setup
        val binding: FragmentPlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        sharedPref = getSharedPref()
        hideKeyboard()

        // Bind layout elements
        binding.apply {
            lifecycleOwner = this@PlayFragment
            varEventsViewModel = this@PlayFragment.genericEventsViewModel

            // Bind fragment rules elements
            fragPlayRules.apply {
                varPlayViewModel = playViewModel
                varEventsViewModel = this@PlayFragment.genericEventsViewModel
                varNameFirstA = sharedPref.getString(getString(R.string.sp_match_name_first_a), "")
                varNameLastA = sharedPref.getString(getString(R.string.sp_match_name_last_a), "")
                varNameFirstB = sharedPref.getString(getString(R.string.sp_match_name_first_b), "")
                varNameLastB = sharedPref.getString(getString(R.string.sp_match_name_last_b), "")
                numberPicker.apply {
                    minValue = 1
                    maxValue = 19
                    value = 2
                    // get an array of odd numbers for the number of frames
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }

                // VM Observers
                this@PlayFragment.genericEventsViewModel.apply {
                    eventGeneralAction.observe(viewLifecycleOwner, EventObserver {
                        when (it) {
                            MatchAction.MATCH_START -> { // When pressing the button to start the match
                                if (sharedPref.isMatchInProgress()) { // If the match is in progress open dialog
                                    findNavController().navigate(
                                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                            MatchAction.MATCH_START_NEW,
                                            MatchAction.NO_ACTION,
                                            MatchAction.MATCH_LOAD
                                        )
                                    )
                                } else { // Else start a new match
                                    matchViewModel.startNewMatch(
                                        varNameFirstA, varNameLastA, varNameFirstB, varNameLastB,
                                        playViewModel.eventFrames.value!!,
                                        playViewModel.reds.value!!,
                                        playViewModel.eventFoulModifier.value!!,
                                        playViewModel.eventBreaksFirst.value!!
                                    )
                                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                                }
                            }
                            MatchAction.MATCH_START_NEW -> { // Start a new match
                                matchViewModel.startNewMatch(
                                    varNameFirstA, varNameLastA, varNameFirstB, varNameLastB,
                                    playViewModel.eventFrames.value!!,
                                    playViewModel.reds.value!!,
                                    playViewModel.eventFoulModifier.value!!,
                                    playViewModel.eventBreaksFirst.value!!
                                )
                                findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                            }
                            MatchAction.MATCH_LOAD -> { // Attempt to load match from database
                                matchViewModel.loadMatchPartAPointToCurrentFrame()
                                findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                            }
                            MatchAction.INFO_FOUL -> { // Open the foul info dialog
                                findNavController().navigate(
                                    PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                        MatchAction.IGNORE,
                                        MatchAction.IGNORE,
                                        MatchAction.INFO_FOUL
                                    )
                                )
                            }
                            else -> { // Empty else req'd
                            }
                        }
                    })
                }
            }
        }
        return binding.root
    }
}