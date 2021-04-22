package com.example.snookerscore.fragments.gamedialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentGameFoulDialogBinding
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.Ball.*
import com.example.snookerscore.fragments.game.BallAdapter
import com.example.snookerscore.fragments.game.BallListener
import com.example.snookerscore.fragments.game.GameFragmentViewModel
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.toast
import java.util.*

class GameFoulDialogFragment : DialogFragment() {
    private lateinit var ballsList: List<Ball>
    private val foulDialogViewModel: GameFoulDialogViewModel by viewModels()
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameFoulDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_foul_dialog, container, false)

        // Bind RV, VM, adapter
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val ballAdapter = BallAdapter(BallListener { ball ->
            foulDialogViewModel.onBallClicked(ball)
        }, MutableLiveData(mutableListOf()))

        binding.apply {
            lifecycleOwner = this@GameFoulDialogFragment
            foulViewModel = foulDialogViewModel
            gameViewModel = gameFragmentViewModel
            foulBallsListRv.apply {
                layoutManager = linearLayoutManager
                adapter = ballAdapter
            }
        }

        // VM Observers
        foulDialogViewModel.apply {
            actionClicked.observe(viewLifecycleOwner, {
                binding.foulActionContinue.isSelected = it == PotAction.SWITCH
                binding.foulActionForceContinue.isSelected = it == PotAction.CONTINUE
            })
            freeBall.observe(viewLifecycleOwner, {
                binding.foulActionFreeBall.isSelected = it
            })
            removeRed.observe(viewLifecycleOwner, {
                binding.foulActionRemoveRed.isSelected = it
            })
            eventFoulNotValid.observe(viewLifecycleOwner, EventObserver {
                requireContext().toast("Select a ball and an action to continue")
            })
            foul.observe(viewLifecycleOwner, EventObserver { pot -> // If foul confirms, send foul to gameFragmentViewModel
                gameFragmentViewModel.handleFoulDialog(pot, foulDialogViewModel.removeRed.value!!, foulDialogViewModel.freeBall.value!!)
                dismiss()
            })
        }
        gameFragmentViewModel.apply {
            eventCancelDialog.observe(viewLifecycleOwner, EventObserver {
                dismiss()
            })
            displayBallStack.observe(viewLifecycleOwner, { ballStack ->
                ballsList = when (ballStack.size) {
                    2 -> listOf(WHITE, BLACK)
                    3 -> listOf(WHITE, PINK, BLACK)
                    4 -> listOf(WHITE, BLUE, PINK, BLACK)
                    5 -> listOf(WHITE, BROWN, BLUE, PINK, BLACK)
                    6 -> listOf(WHITE, GREEN, BROWN, BLUE, PINK, BLACK)
                    7 -> listOf(WHITE, YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
                    else -> listOf(WHITE, RED, YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
                }
                ballAdapter.submitList(ballsList)
            })
        }
        return binding.root
    }
}