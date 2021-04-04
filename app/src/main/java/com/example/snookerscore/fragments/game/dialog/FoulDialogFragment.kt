package com.example.snookerscore.fragments.game.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentFoulDialogBinding
import com.example.snookerscore.fragments.game.*
import com.example.snookerscore.fragments.game.ShotType.FOUL
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.toast
import timber.log.Timber

class FoulDialogFragment : DialogFragment() {
    private lateinit var ballsList: List<Pair<Ball, ShotType>>
    private val foulDialogViewModel: FoulDialogViewModel by viewModels()
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels {
        GameFragmentViewModelFactory(requireNotNull(this.activity).application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFoulDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_foul_dialog, container, false)

        // Bind RV, VM, adapter
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val ballAdapter = BallAdapter(BallListener { ball, shotType ->
            foulDialogViewModel.onBallClicked(ball, shotType)
        })
        Balls.apply { ballsList = listOf(Pair(WHITE, FOUL), Pair(RED, FOUL), Pair(YELLOW, FOUL), Pair(GREEN, FOUL), Pair(BROWN, FOUL), Pair(BLUE, FOUL), Pair(PINK, FOUL), Pair(BLACK, FOUL)) }
        binding.apply {
            lifecycleOwner = this@FoulDialogFragment
            gameViewModel = gameFragmentViewModel
            foulViewModel = foulDialogViewModel
            foulBallsListRv.apply {
                layoutManager = linearLayoutManager
                adapter = ballAdapter
                ballAdapter.submitList(ballsList)
            }
            foulActions = Actions
        }
//        gameFragmentViewModel.isFoulDialogOpen.value = true

        // VM Observers
        foulDialogViewModel.apply {
            eventCancelDialog.observe(viewLifecycleOwner, EventObserver {
                dismiss()
            })
            eventFoulNotValid.observe(viewLifecycleOwner, EventObserver {
                requireContext().toast("Select a ball and an action to continue")
            })
            foul.observe(viewLifecycleOwner, EventObserver {
                Timber.e("foul logged")
            })
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
//        gameFragmentViewModel.isFoulDialogOpen.value = false
    }
}