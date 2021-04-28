package com.example.snookerscore.fragments.play

import android.content.Context
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
import com.example.snookerscore.databinding.FragmentPlayBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.utils.EventObserver

class PlayFragment : androidx.fragment.app.Fragment() {

    private val playFragmentViewModel: PlayFragmentViewModel by viewModels()
    private val gameViewModel: GameViewModel by activityViewModels()
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPlayBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)

        sharedPref = requireActivity().application.getSharedPreferences(
            requireActivity().getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        binding.apply {
            viewModel = playFragmentViewModel
            numberPicker.apply {
                minValue = 1
                maxValue = 19
                value = 2
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
            }

            // Show the selected button corresponding to it's selected value
            playFragmentViewModel.apply {
                eventReds.observe(viewLifecycleOwner, EventObserver {
                    fragPlayBtnRedsSix.isSelected = it == 6
                    fragPlayBtnRedsTen.isSelected = it == 10
                    fragPlayBtnRedsFifteen.isSelected = it == 15
                })
                eventFoulModifier.observe(viewLifecycleOwner, EventObserver {
                    fragPlayBtnFoulOne.isSelected = it == -3
                    fragPlayBtnFoulTwo.isSelected = it == -2
                    fragPlayBtnFoulThree.isSelected = it == -1
                    fragPlayBtnFoulFour.isSelected = it == 0
                })
                eventBreaksFirst.observe(viewLifecycleOwner, EventObserver {
                    fragPlayBtnBreakPlayerA.isSelected = it == 0
                    fragPlayBtnBreakPlayerB.isSelected = it == 1
                })
            }

            // If match is saved open dialog, else start a new match
            fragPlayBtnPlay.setOnClickListener {
                if (sharedPref.getBoolean(requireContext().getString(R.string.shared_pref_match_is_saved), false)) {
                    sharedPref.edit().putBoolean(getString(R.string.shared_pref_match_is_saved), false).apply()
                    findNavController().navigate(
                        PlayFragmentDirections.actionPlayFragmentToGameGenericDialogFragment(
                            MatchAction.MATCH_CONTINUE,
                            MatchAction.MATCH_START_NEW
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
                    when (it) {
                        MatchAction.MATCH_START_NEW -> resetMatch()
                        MatchAction.MATCH_CONTINUE -> gameViewModel.getSavedStateRules()
                        else -> {
                        }
                    }
                    findNavController().navigate(PlayFragmentDirections.actionPlayFragmentToGameFragment())
                })
            }
        }
        return binding.root
    }

    private fun resetMatch() = sharedPref.edit().apply {
        putInt(getString(R.string.shared_pref_match_frames), playFragmentViewModel.eventFrames.value!!.peekContent())
        putInt(getString(R.string.shared_pref_match_reds), playFragmentViewModel.eventReds.value!!.peekContent())
        putInt(getString(R.string.shared_pref_match_foul), playFragmentViewModel.eventFoulModifier.value!!.peekContent())
        putInt(getString(R.string.shared_pref_match_first), playFragmentViewModel.eventBreaksFirst.value!!.peekContent())
        apply()
        gameViewModel.getSavedStateRules()
        gameViewModel.resetMatchScore()
    }
}