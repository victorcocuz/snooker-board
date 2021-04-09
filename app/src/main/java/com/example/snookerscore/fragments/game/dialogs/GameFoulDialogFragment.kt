package com.example.snookerscore.fragments.game.dialogs

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
import com.example.snookerscore.databinding.FragmentGameFoulDialogBinding
import com.example.snookerscore.fragments.game.*
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.toast

class GameFoulDialogFragment : DialogFragment() {
    private lateinit var ballsList: List<Ball>
    private val foulDialogViewModel: GameFoulDialogViewModel by viewModels()
    private val gameFragmentViewModel: GameFragmentViewModel by activityViewModels {
        GameFragmentViewModelFactory(requireNotNull(this.activity).application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameFoulDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game_foul_dialog, container, false)

        // Bind RV, VM, adapter
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val ballAdapter = BallAdapter(BallListener { ball ->
            foulDialogViewModel.onBallClicked(ball)
        })
        binding.apply {
            lifecycleOwner = this@GameFoulDialogFragment
            foulViewModel = foulDialogViewModel
            gameViewModel = gameFragmentViewModel
            foulBallsListRv.apply {
                layoutManager = linearLayoutManager
                adapter = ballAdapter

            }
            foulActions = ShotActions
        }

        // VM Observers
        foulDialogViewModel.apply {
            eventFoulNotValid.observe(viewLifecycleOwner, EventObserver {
                requireContext().toast("Select a ball and an action to continue")
            })
            foul.observe(viewLifecycleOwner, EventObserver { pot -> // If foul confirms, send foul to gameFragmentViewModel
                gameFragmentViewModel.handleFoulDialog(pot, foulDialogViewModel.removeRed, foulDialogViewModel.freeBall)
                dismiss()
            })
        }
        gameFragmentViewModel.apply{
            eventCancelDialog.observe(viewLifecycleOwner, EventObserver {
                dismiss()
            })
            ballStackSize.observe(viewLifecycleOwner, { ballStackSize ->
                Balls.apply {
                    ballsList = when(ballStackSize) {
                        2 -> listOf(WHITE, BLACK)
                        3 -> listOf(WHITE, PINK, BLACK)
                        4 -> listOf(WHITE, BLUE, PINK, BLACK)
                        5 -> listOf(WHITE, BROWN, BLUE, PINK, BLACK)
                        6 -> listOf(WHITE, GREEN, BROWN, BLUE, PINK, BLACK)
                        7 -> listOf(WHITE, YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
                        else -> listOf(WHITE, RED, YELLOW, GREEN, BROWN, BLUE, PINK, BLACK)
                    }
                }
                ballAdapter.submitList(ballsList)
            })
        }
        return binding.root
    }
}