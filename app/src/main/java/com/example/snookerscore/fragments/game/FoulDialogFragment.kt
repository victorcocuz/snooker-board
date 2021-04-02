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
    //    val viewModelFactory = GameFragmentViewModelFactory(requireNotNull(this.activity).application)
//    ViewModelProvider(this, viewModelFactory).get(GameViewModel::class.java)
    private val viewModel: GameFragmentViewModel by activityViewModels()

    //    private val foulDialogViewModel = ViewModelProvider(this).get(FoulDialogViewModel::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentFoulDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_foul_dialog, container, false)
        binding.apply {
            lifecycleOwner = this@FoulDialogFragment
            gameViewModel = viewModel
            foulBalls.gameViewModel = gameViewModel
            foulBalls.apply {
                balls = Balls
            }
            foulActions = FoulActions
        }
        viewModel.isFoulDialogOpen.value = true

        viewModel.foulCheck.observe(viewLifecycleOwner, Observer { foulCheck ->
            if (!foulCheck) dismiss()
        })
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.isFoulDialogOpen.value = false
    }
}