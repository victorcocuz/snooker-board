package com.example.snookerscore.fragments.gamedialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameGenDialogBinding
import com.example.snookerscore.fragments.game.GameFragmentViewModel
import com.example.snookerscore.fragments.game.MatchAction
import com.example.snookerscore.utils.EventObserver

class GameGenericDialogFragment : DialogFragment() {
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameGenDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_gen_dialog, container, false)
        val action = GameGenericDialogFragmentArgs.fromBundle(requireArguments()).matchAction
        isCancelable = false

        binding.apply {
            lifecycleOwner = this@GameGenericDialogFragment
            gameViewModel = gameFragmentViewModel
            matchAction = action
        }

        // Observers
        gameFragmentViewModel.apply {
            eventCancelDialog.observe(viewLifecycleOwner, EventObserver {
                dismiss()
            })
            eventMatchActionConfirmed.observe(viewLifecycleOwner, EventObserver { matchAction ->
                dismiss()
                when (matchAction) {
                    MatchAction.CANCEL_MATCH -> {
                        resetMatch()
                        findNavController().navigate(GameGenericDialogFragmentDirections.actionGameGenericDialogFragmentToPlayFragment())
                    }
                    in listOf(MatchAction.END_FRAME, MatchAction.FRAME_ENDED) -> frameEnded()
                    in listOf(MatchAction.END_MATCH, MatchAction.MATCH_ENDED) -> {
                        matchEnded()
                        findNavController().navigate(GameGenericDialogFragmentDirections.actionGameGenericDialogFragmentToGameStatsFragment())
                    }
                    else -> {}
                }
            })
        }
        return binding.root
    }
}