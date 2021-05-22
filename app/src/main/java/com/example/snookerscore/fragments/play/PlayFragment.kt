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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PlayFragment : androidx.fragment.app.Fragment() {
    private val fragmentScope = CoroutineScope(Dispatchers.Default)
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
            playViewModel = playFragmentViewModel
            eventsViewModel = this@PlayFragment.eventsViewModel
            varNameA = getNameFromPreferences(getString(R.string.shared_pref_match_player_a_name))
            varNameB = getNameFromPreferences(getString(R.string.shared_pref_match_player_b_name))
            numberPicker.apply {
                minValue = 1
                maxValue = 19
                value = 2
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
            }

            // If match is saved open dialog, else start a new match
            fragPlayBtnPlay.setOnClickListener {
                if (sharedPref.getBoolean(requireContext().getString(R.string.shared_pref_match_is_in_progress), false)) {
                    findNavController().navigate(
                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                            MatchAction.MATCH_START_NEW,
                            MatchAction.NO_ACTION,
                            MatchAction.MATCH_RELOAD
                        )
                    )
                } else {
                    startNewMatch()
                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                }
            }

            // When new match is selected reset match, otherwise continue the existing match
            this@PlayFragment.eventsViewModel.apply {
                eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver {
                    when (it) {
                        MatchAction.MATCH_START_NEW -> {
                            startNewMatch()
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
                        MatchAction.NAME_CHANGE_CONFIRM -> {
                            varNameA = getNameFromPreferences(getString(R.string.shared_pref_match_player_a_name))
                            varNameB = getNameFromPreferences(getString(R.string.shared_pref_match_player_b_name))
                        }
                        else -> {
                        }
                    }
                })
            }
        }
        return binding.root
    }

    private fun getNameFromPreferences(playerName: String): String? {
        if (sharedPref.getString(playerName, "") == "") {
            sharedPref.edit().putString(
                playerName, when (playerName) {
                    getString(R.string.shared_pref_match_player_a_name) -> getString(R.string.fragment_play_btn_player_a)
                    else -> getString(R.string.fragment_play_btn_player_b)
                }
            ).apply()
        }
        return sharedPref.getString(playerName, "")
    }

    private fun startNewMatch() = sharedPref.edit().apply {
        putInt(getString(R.string.shared_pref_match_frames), playFragmentViewModel.eventFrames.value!!)
        putInt(getString(R.string.shared_pref_match_reds), playFragmentViewModel.reds.value!!)
        putInt(getString(R.string.shared_pref_match_foul), playFragmentViewModel.eventFoulModifier.value!!)
        putInt(getString(R.string.shared_pref_match_first), playFragmentViewModel.eventBreaksFirst.value!!)
        apply()
        gameViewModel.startNewMatch()
    }
}