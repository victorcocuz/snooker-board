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
import com.example.snookerscore.databinding.FragmentPlayBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.repository.SnookerRepository
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.getSharedPref

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

        sharedPref = requireActivity().getSharedPref()

        binding.apply {
            lifecycleOwner = this@PlayFragment
            playViewModel = playFragmentViewModel
            numberPicker.apply {
                minValue = 1
                maxValue = 19
                value = 2
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
            }
            snookerRepository = SnookerRepository(SnookerDatabase.getDatabase(requireActivity().application))

            // If match is saved open dialog, else start a new match
            fragPlayBtnPlay.setOnClickListener {
                if (sharedPref.getBoolean(requireContext().getString(R.string.shared_pref_match_is_saved), false)) {
                    findNavController().navigate(
                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                            MatchAction.MATCH_START_NEW,
                            MatchAction.NO_ACTION,
                            MatchAction.MATCH_RELOAD
                        )
                    )
                } else {
                    resetMatch()
                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                }
            }

            // When new match is selected reset match, otherwise continue the existing match
            eventsViewModel.apply {
                eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver {
                    sharedPref.edit().putBoolean(getString(R.string.shared_pref_match_is_saved), false).apply()
                    if (it == MatchAction.MATCH_START_NEW) {
                        resetMatch()
                        onEventStartMatch()
                    }
                    if (it == MatchAction.MATCH_RELOAD) {
                        snookerRepository.currentFrame.observe(viewLifecycleOwner, { domainFrame ->
                            gameViewModel.loadMatch(domainFrame)
                            onEventStartMatch()
                        })
                    }
                })
                eventStartMatch.observe(viewLifecycleOwner, EventObserver {
                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                })
            }
        }
        return binding.root
    }

    private fun resetMatch() = sharedPref.edit().apply {
        putInt(getString(R.string.shared_pref_match_frames), playFragmentViewModel.eventFrames.value!!)
        putInt(getString(R.string.shared_pref_match_reds), playFragmentViewModel.reds.value!!)
        putInt(getString(R.string.shared_pref_match_foul), playFragmentViewModel.eventFoulModifier.value!!)
        putInt(getString(R.string.shared_pref_match_first), playFragmentViewModel.eventBreaksFirst.value!!)
        apply()
        gameViewModel.resetMatch()
    }
}