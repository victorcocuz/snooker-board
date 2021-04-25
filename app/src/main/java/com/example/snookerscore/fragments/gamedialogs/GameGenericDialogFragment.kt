package com.example.snookerscore.fragments.gamedialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameGenDialogBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.utils.EventObserver

class GameGenericDialogFragment : DialogFragment() {
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private lateinit var matchAction: MatchAction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameGenDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_gen_dialog, container, false)
        isCancelable = false

        binding.apply {
            lifecycleOwner = this@GameGenericDialogFragment
            genericEventsViewModel = eventsViewModel
            matchActionYes = GameGenericDialogFragmentArgs.fromBundle(requireArguments()).matchActionYes
            matchActionNo = GameGenericDialogFragmentArgs.fromBundle(requireArguments()).matchActionNo
        }

        // Observers
        eventsViewModel.eventMatchActionQueried.observe(viewLifecycleOwner, EventObserver {
            dismiss()
            matchAction = it
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        eventsViewModel.onEventMatchActionConfirmed(matchAction)
    }
}