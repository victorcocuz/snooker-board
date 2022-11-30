package com.quickpoint.snookerboard.fragments.gamedialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.quickpoint.snookerboard.DialogViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentDialogFoulBinding
import com.quickpoint.snookerboard.fragments.game.BallAdapter
import com.quickpoint.snookerboard.fragments.game.BallListener
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

class FoulDialogFragment : DialogFragment() {
    private val dialogVm: DialogViewModel by activityViewModels()
    private lateinit var gameVm: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutSizeByFactor(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentDialogFoulBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_foul, container, false)
        gameVm = ViewModelProvider(requireParentFragment().childFragmentManager.fragments[0])[GameViewModel::class.java]

        // Bind all required elements from the view
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varGameVm = this@FoulDialogFragment.gameVm
            varDialogVm = this@FoulDialogFragment.dialogVm
            foulBallsListRv.apply {
                layoutManager = GridLayoutManager(activity, 4)
                adapter = BallAdapter(
                    BallListener { ball -> dialogVm.onBallClicked(ball) },
                    MutableLiveData(),
                    BallAdapterType.FOUL
                )
            }
        }

        // Observers
        dialogVm.apply {
            eventDialogAction.observe(viewLifecycleOwner, EventObserver { action ->
                when (action) {
                    FOUL_ATTEMPT ->
                        if (foulIsValid()) {
                            gameVm.onEventGameAction(FOUL_CONFIRM)
                            dismiss()
                        }
                        else toast(getString(R.string.toast_foul_invalid))
                    else -> {
                        dialogVm.resetFoul()
                        dismiss()
                    }
                }
            })
        }
        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        dialogVm.resetFoul()
        super.onCancel(dialog)
    }
}