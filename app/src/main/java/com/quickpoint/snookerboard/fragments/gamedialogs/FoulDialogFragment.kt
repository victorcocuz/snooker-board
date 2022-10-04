package com.quickpoint.snookerboard.fragments.gamedialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentDialogFoulBinding
import com.quickpoint.snookerboard.fragments.game.BallAdapter
import com.quickpoint.snookerboard.fragments.game.BallListener
import com.quickpoint.snookerboard.utils.*

class FoulDialogFragment : DialogFragment() {
    private val dialogViewModel: DialogViewModel by activityViewModels()
    private val matchViewModel: MatchViewModel by activityViewModels()
    private lateinit var matchAction: MatchAction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutSizeByFactor(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentDialogFoulBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_foul, container, false)

        // Bind RV, VM, adapter
        val ballAdapter = BallAdapter(
            BallListener { ball -> dialogViewModel.onBallClicked(ball) },
            MutableLiveData(),
            BallAdapterType.FOUL
        )

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varMatchViewModel = this@FoulDialogFragment.matchViewModel
            varDialogViewModel = this@FoulDialogFragment.dialogViewModel
            foulBallsListRv.apply {
                layoutManager = GridLayoutManager(activity, 4)
                adapter = ballAdapter
            }
        }

        // Observers
        dialogViewModel.apply {
            eventDialogAction.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    MatchAction.FOUL_QUERY -> {
                        if (foulIsValid()) {
                            matchAction = MatchAction.FOUL_CONFIRM
                            dismiss()
                        } else toast(getString(R.string.toast_foul_invalid))
                    }
                    else -> {
                        dialogViewModel.resetFoul()
                        matchAction = it
                        dismiss()
                    }
                }
            })
        }
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dialogViewModel.resetFoul()
    }

    override fun onDestroy() { // Pass match action to view model
        super.onDestroy()
        if (this::matchAction.isInitialized) matchViewModel.assignEventMatchAction(matchAction)
    }


}