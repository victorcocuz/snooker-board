package com.quickpoint.snookerboard.fragments.gamedialogs

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentDialogGenBinding
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.MatchAction
import com.quickpoint.snookerboard.utils.setLayoutSizeByFactor


class GameGenericDialogFragment : DialogFragment() {
    private val dialogViewModel: DialogViewModel by activityViewModels()
    private val matchViewModel: MatchViewModel by activityViewModels()
    private lateinit var matchAction: MatchAction

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLayoutSizeByFactor(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDialogGenBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_gen, container, false)

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = this@GameGenericDialogFragment
            varGenericEventsViewModel = dialogViewModel
            varMatchViewModel = this@GameGenericDialogFragment.matchViewModel
            GameGenericDialogFragmentArgs.fromBundle(requireArguments()).apply {
                varDialogMatchActionA = matchActionA
                varDialogMatchActionB = matchActionB
                varDialogMatchActionC = matchActionC
                if (varDialogMatchActionC in listOf(MatchAction.MATCH_ENDED, MatchAction.FRAME_ENDED)) {
                    this@GameGenericDialogFragment.isCancelable = false // Action must be taken if game or match are ended
                }
            }
        }

        // Observers
        dialogViewModel.eventDialogAction.observe(viewLifecycleOwner, EventObserver {
            dismiss() // Close dialog once a match action as been clicked on
            matchAction = it // Save the recorded match action to be passed on in onDestroy
        })
        return binding.root
    }

    override fun onDestroy() { // Pass match action to view model
        super.onDestroy()
        if (this::matchAction.isInitialized) matchViewModel.assignEventMatchAction(matchAction)
    }
}

