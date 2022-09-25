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
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentPlayBinding
import com.quickpoint.snookerboard.utils.*

class PlayFragment : androidx.fragment.app.Fragment() {
    // Variables
    private val playViewModel: PlayViewModel by viewModels()
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
            varMatchViewModel = matchViewModel

            // Bind fragment rules elements
            fragPlayRules.apply {
                varPlayViewModel = playViewModel
                varMatchViewModel = matchViewModel
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
                matchViewModel.eventMatchAction.observe(viewLifecycleOwner, EventObserver { matchAction ->
                    sharedPref.edit().apply {
                        resources.apply {
                            putString(getString(R.string.sp_match_name_first_a), varNameFirstA ?: "")
                            putString(getString(R.string.sp_match_name_last_a), varNameLastA ?: "")
                            putString(getString(R.string.sp_match_name_first_b), varNameFirstB ?: "")
                            putString(getString(R.string.sp_match_name_last_b), varNameLastB ?: "")
                            putInt(getString(R.string.sp_match_frames), playViewModel.eventFrames.value!!)
                            putInt(getString(R.string.sp_match_reds), playViewModel.reds.value!!)
                            putInt(getString(R.string.sp_match_foul), playViewModel.eventFoulModifier.value!!)
                            putInt(getString(R.string.sp_match_first), playViewModel.eventBreaksFirst.value!!)
                            apply()
                        }
                    }
                    when (matchAction) {
                        MatchAction.INFO_FOUL_DIALOG -> { // Open the foul info dialog
                            findNavController().navigate(
                                PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                    MatchAction.IGNORE,
                                    MatchAction.IGNORE,
                                    MatchAction.INFO_FOUL_DIALOG
                                )
                            )
                        }
                        MatchAction.MATCH_START_DIALOG -> { // When pressing the button to start the match
                            findNavController().navigate(
                                PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                    MatchAction.MATCH_START,
                                    MatchAction.IGNORE,
                                    MatchAction.MATCH_LOAD
                                )
                            )
                        }
                        in listOf(MatchAction.MATCH_START, MatchAction.MATCH_LOAD) -> {
                            findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment(matchAction))
                        }
                        else -> {
                        }
                    }
                })
            }
        }
        return binding.root
    }
}