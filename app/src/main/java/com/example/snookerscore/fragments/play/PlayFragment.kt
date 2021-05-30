package com.example.snookerscore.fragments.play

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.database.SnookerDatabase
import com.example.snookerscore.database.asDomainFrame
import com.example.snookerscore.databinding.FragmentPlayBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.getSharedPref
import com.example.snookerscore.utils.hideKeyboard

class PlayFragment : androidx.fragment.app.Fragment() {
    private val playFragmentViewModel: PlayFragmentViewModel by viewModels()
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var snookerRepository: SnookerRepository
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPlayBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)

        hideKeyboard()

        snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))
        sharedPref = requireActivity().getSharedPref()

        binding.apply {
            lifecycleOwner = this@PlayFragment
            varEventsViewModel = this@PlayFragment.eventsViewModel

            fragPlayRules.apply {
                varPlayViewModel = playFragmentViewModel
                varEventsViewModel = this@PlayFragment.eventsViewModel
                varNameFirstA = sharedPref.getString(getString(R.string.shared_pref_match_name_first_a), "")
                varNameLastA = sharedPref.getString(getString(R.string.shared_pref_match_name_last_a), "")
                varNameFirstB = sharedPref.getString(getString(R.string.shared_pref_match_name_first_b), "")
                varNameLastB = sharedPref.getString(getString(R.string.shared_pref_match_name_last_b), "")
                numberPicker.apply {
                    minValue = 1
                    maxValue = 19
                    value = 2
                    displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                }

                // When new match is selected reset match, otherwise continue the existing match
                this@PlayFragment.eventsViewModel.apply {
                    eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver {
                        when (it) {
                            // If match is saved open dialog, else start a new match
                            MatchAction.MATCH_START -> {
                                if (sharedPref.getBoolean(requireContext().getString(R.string.shared_pref_match_is_in_progress), false)) {
                                    findNavController().navigate(
                                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                            MatchAction.MATCH_START_NEW,
                                            MatchAction.NO_ACTION,
                                            MatchAction.MATCH_RELOAD
                                        )
                                    )
                                } else {
                                    startNewMatch(varNameFirstA, varNameLastA, varNameFirstB, varNameLastB)
                                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                                }
                            }
                            MatchAction.MATCH_START_NEW -> {
                                startNewMatch(varNameFirstA, varNameLastA, varNameFirstB, varNameLastB)
                                findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                            }
                            MatchAction.MATCH_RELOAD -> {
                                snookerRepository.searchByCount(sharedPref.getInt(getString(R.string.shared_pref_match_crt_frame), 0))
                                snookerRepository.crtFrame.observe(viewLifecycleOwner, { domainFrame ->
                                    gameViewModel.loadMatch(domainFrame?.asDomainFrame())
                                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                                })
                            }
                            in listOf(MatchAction.NAME_CHANGE_A_QUERIED, MatchAction.NAME_CHANGE_B_QUERIED) -> {
                                findNavController().navigate(
                                    PlayFragmentDirections.actionPlayFragmentToTextDialogFragment(it)
                                )
                            }
                            MatchAction.INFO_FOUL -> {
                                findNavController().navigate(
                                    PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                                        MatchAction.IGNORE,
                                        MatchAction.IGNORE,
                                        MatchAction.INFO_FOUL
                                    )
                                )
                            }
                            else -> {
                            }
                        }
                    })
                }
            }
        }
        return binding.root
    }

    private fun startNewMatch(nameFirstA: String?, nameLastA: String?, nameFirstB: String?, nameLastB: String?) = sharedPref.edit().apply {
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