package com.quickpoint.snookerboard.fragments.gamedialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentDialogGenBinding
import com.quickpoint.snookerboard.fragments.game.GameFragment
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.listOfMatchActionsUncancelable
import com.quickpoint.snookerboard.utils.setLayoutSizeByFactor


class GenericDialogFragment : DialogFragment() {
    private val dialogVm: DialogViewModel by activityViewModels()
    private var gameVm: GameViewModel? = null
    private lateinit var matchAction: MatchAction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutSizeByFactor(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentDialogGenBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_gen, container, false)

        if (requireParentFragment().childFragmentManager.fragments[0] is GameFragment)
            gameVm = ViewModelProvider(requireParentFragment().childFragmentManager.fragments[0])[GameViewModel::class.java]

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varDialogVm = dialogVm
            varGameVm = this@GenericDialogFragment.gameVm
            GenericDialogFragmentArgs.fromBundle(requireArguments()).apply {
                varActionA = matchActionA
                varActionB = matchActionB
                varActionC = matchActionC
                if (varActionC in listOfMatchActionsUncancelable) {
                    this@GenericDialogFragment.isCancelable = false // An action has to be taken if game or match are ended
                }
            }
        }

        // Observers
        dialogVm.eventDialogAction.observe(viewLifecycleOwner, EventObserver { action ->
            matchAction = action
            dismiss() // Close dialog once a match action as been clicked on
        })


        return binding.root
    }

    override fun onDestroy() { // Pass action back here to avoid crash during navigation
        super.onDestroy()
        if (this::matchAction.isInitialized) gameVm?.onEventGameAction(matchAction, when(matchAction) {
            MATCH_CANCEL, FRAME_RERACK, FRAME_START_NEW ->  true
            else -> false
        })
    }
}

