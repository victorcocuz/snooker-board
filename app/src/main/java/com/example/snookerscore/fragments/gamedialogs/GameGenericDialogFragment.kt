package com.example.snookerscore.fragments.gamedialogs

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameGenDialogBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.fragments.game.GameViewModel
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.setSize


class GameGenericDialogFragment : DialogFragment() {
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var matchAction: MatchAction

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSize(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameGenDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_gen_dialog, container, false)

        binding.apply {
            lifecycleOwner = this@GameGenericDialogFragment
            genericEventsViewModel = eventsViewModel
            gameViewModel = this@GameGenericDialogFragment.gameViewModel
            GameGenericDialogFragmentArgs.fromBundle(requireArguments()).apply {
                dialogMatchActionA = matchActionA
                dialogMatchActionB = matchActionB
                dialogMatchActionC = matchActionC
            }
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
        if (this::matchAction.isInitialized) eventsViewModel.onEventMatchActionConfirmed(matchAction)
    }
}

