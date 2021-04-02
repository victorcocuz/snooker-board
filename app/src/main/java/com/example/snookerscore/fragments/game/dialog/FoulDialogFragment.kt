package com.example.snookerscore.fragments.game.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentFoulDialogBinding
import com.example.snookerscore.fragments.game.Balls
import com.example.snookerscore.fragments.game.FoulActions
import com.example.snookerscore.fragments.game.GameFragmentViewModel
import com.example.snookerscore.fragments.game.GameFragmentViewModelFactory
import com.example.snookerscore.utils.toast

class FoulDialogFragment : DialogFragment() {
    private val foulDialogViewModel: FoulDialogFragment by viewModels()
    private val viewModel: GameFragmentViewModel by activityViewModels { GameFragmentViewModelFactory(requireNotNull(this.activity).application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFoulDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_foul_dialog, container, false)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val ballAdapter = FoulDialogAdapter(FoulDialogListener { ballType ->
            requireContext().toast("whatever $ballType")
        })
        binding.apply {
            lifecycleOwner = this@FoulDialogFragment
            gameViewModel = viewModel
            foulBallsList.apply {
                layoutManager = linearLayoutManager
                adapter = ballAdapter
                ballAdapter.submitList(
                    listOf(
                        Balls.WHITE,
                        Balls.RED,
                        Balls.YELLOW,
                        Balls.GREEN,
                        Balls.BROWN,
                        Balls.BLUE,
                        Balls.PINK,
                        Balls.BLACK
                    )
                )
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