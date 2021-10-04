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
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.databinding.FragmentPlayBinding
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*

class PlayFragment : androidx.fragment.app.Fragment() {
    // Variables
    private val gameViewModel: GameViewModel by activityViewModels()
    private val genericEventsViewModel: GenericEventsViewModel by activityViewModels()
    private val playFragmentViewModel: PlayFragmentViewModel by viewModels()
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Bind layout, initial setup
        val binding: FragmentPlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))
        sharedPref = requireActivity().getSharedPref()
        hideKeyboard()

        // Bind layout elements
        binding.apply {
            lifecycleOwner = this@PlayFragment
            varEventsViewModel = this@PlayFragment.genericEventsViewModel

            // Bind fragment rules elements
            fragPlayRules.apply {
                varPlayViewModel = playFragmentViewModel
                varEventsViewModel = this@PlayFragment.genericEventsViewModel
                varNameFirstA = sharedPref.getString(getString(R.string.shared_pref_match_name_first_a), "")
                varNameLastA = sharedPref.getString(getString(R.string.shared_pref_match_name_last_a), "")
                varNameFirstB = sharedPref.getString(getString(R.string.shared_pref_match_name_first_b), "")
                varNameLastB = sharedPref.getString(getString(R.string.shared_pref_match_name_last_b), "")
                numberPicker.apply {
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }
                        .toTypedArray() // get an array of odd numbers for the number of frames
                }

                // Observe eventMatchActionConfirmed
                this@PlayFragment.genericEventsViewModel.apply {
                    eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver {
                        when (it) {
                            MatchAction.MATCH_START -> { // When pressing the button to start the match
                                if (sharedPref.getBoolean(
                                        requireContext().getString(R.string.shared_pref_match_is_in_progress),
                                        false
                                    )
                                ) { // If the match is in progress open dialog
                                    findNavController().navigate(
                                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                            MatchAction.MATCH_START_NEW,
                                            MatchAction.NO_ACTION,
                                            MatchAction.MATCH_LOAD
                                        )
                                    )
                                } else { // Else start a new match
                                    startNewMatch(varNameFirstA, varNameLastA, varNameFirstB, varNameLastB)
                                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                                }
                            }
                            MatchAction.MATCH_START_NEW -> { // Start a new match
                                startNewMatch(varNameFirstA, varNameLastA, varNameFirstB, varNameLastB)
                                findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                            }
                            MatchAction.MATCH_LOAD -> { // Reload an existing match
                                snookerRepository.searchByCount(sharedPref.getInt(getString(R.string.shared_pref_match_crt_frame), 0))
                                snookerRepository.crtFrame.observe(viewLifecycleOwner, { domainFrame ->
                                    gameViewModel.loadMatch(domainFrame?.asDomainFrame())
                                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                                })
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

    // When starting a new match, add new shared prefs, trigger the startNewMatch within frameViewModel
    private fun startNewMatch(nameFirstA: String?, nameLastA: String?, nameFirstB: String?, nameLastB: String?) {
        sharedPref.edit().apply {
            putString(getString(R.string.shared_pref_match_name_first_a), nameFirstA ?: "")
            putString(getString(R.string.shared_pref_match_name_last_a), nameLastA ?: "")
            putString(getString(R.string.shared_pref_match_name_first_b), nameFirstB ?: "")
            putString(getString(R.string.shared_pref_match_name_last_b), nameLastB ?: "")
            putInt(getString(R.string.shared_pref_match_frames), playFragmentViewModel.eventFrames.value!!)
            putInt(getString(R.string.shared_pref_match_reds), playFragmentViewModel.reds.value!!)
            putInt(getString(R.string.shared_pref_match_foul), playFragmentViewModel.eventFoulModifier.value!!)
            putInt(getString(R.string.shared_pref_match_first), playFragmentViewModel.eventBreaksFirst.value!!)
            apply()
            gameViewModel.startNewMatch()
        }
    }
}