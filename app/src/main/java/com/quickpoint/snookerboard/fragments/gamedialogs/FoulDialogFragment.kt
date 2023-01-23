package com.quickpoint.snookerboard.fragments.gamedialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextHeadline
import com.quickpoint.snookerboard.databinding.FragmentDialogFoulBinding
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.EventObserver
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.setLayoutSizeByFactor
import com.quickpoint.snookerboard.utils.toast

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
            varGameVm = gameVm
            varDialogVm = dialogVm

//            fDialogFoulRvBalls.apply {
//                layoutManager = GridLayoutManager(activity, 4)
//                adapter = BallAdapter(
//                    BallListener { ball -> dialogVm.onBallClicked(ball) },
//                    MutableLiveData(),
//                    BallAdapterType.FOUL
//                )
//            }
        }

        // Observers
        dialogVm.apply {
            eventDialogAction.observe(viewLifecycleOwner, EventObserver { action ->
                when (action) {
                    FOUL_ATTEMPT ->
                        if (foulIsValid()) {
                            repeat(eventDialogReds.value!!) { gameVm.onEventGameAction(FRAME_REMOVE_RED, true) }
                            gameVm.onEventGameAction(FOUL_CONFIRM, true)
                            if (Toggle.FreeBall.isEnabled) gameVm.onEventGameAction(FRAME_FREE_ACTIVE, true)
                            dismiss()
                        } else toast(getString(R.string.toast_f_dialog_foul_invalid))
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

@Composable
fun FragmentDialogFoul(
    navController: NavController,
) {
    FragmentColumn() {
        TextHeadline("Dialog Foul")
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentDialogFoulPreview() {
    FragmentDialogFoul(rememberNavController())
}