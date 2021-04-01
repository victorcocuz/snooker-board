package com.example.snookerscore.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentFoulDialogBinding

class FoulDialogFragment : DialogFragment() {
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentFoulDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_foul_dialog, container, false)
        binding.apply {
            lifecycleOwner = this@FoulDialogFragment
            gameViewModel = viewModel
            foulBalls.gameViewModel = viewModel
            foulBalls.apply {
                balls = Balls
                polarity = -1
            }
            foulActions = FoulActions
        }

        viewModel.foulCheck.observe(viewLifecycleOwner, Observer { foulCheck ->
            if (!foulCheck) dismiss()
        })
        return binding.root
    }

}