package com.example.snookerscore.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentPlayBinding
import com.example.snookerscore.utils.EventObserver

class PlayFragment : Fragment() {

    private val playFragmentViewModel: PlayFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPlayBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)

        binding.apply {
            viewModel = playFragmentViewModel
            numberPicker.apply {
                minValue = 1
                maxValue = 19
                value = 2
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
            }

            playFragmentViewModel.apply {
                eventReds.observe(viewLifecycleOwner, EventObserver {
                    fragPlayRedsSix.isSelected = it == 6
                    fragPlayRedsTen.isSelected = it == 10
                    fragPlayRedsFifteen.isSelected = it == 15
                })
                eventFoulModifier.observe(viewLifecycleOwner, EventObserver {
                    fragPlayBtnFoulOne.isSelected = it == -3
                    fragPlayBtnFoulTwo.isSelected = it == -2
                    fragPlayBtnFoulThree.isSelected = it == -1
                    fragPlayBtnFoulFour.isSelected = it == 0
                })
            }
            fragPlayBtnPlay.setOnClickListener {
                it.findNavController().navigate(
                    PlayFragmentDirections.actionPlayFragmentToGameFragment(
                        viewModel!!.eventFrames.value!!.peekContent(),
                        viewModel!!.eventReds.value!!.peekContent(),
                        viewModel!!.eventFoulModifier.value!!.peekContent()
                    )
                )
            }
        }
        return binding.root
    }
}