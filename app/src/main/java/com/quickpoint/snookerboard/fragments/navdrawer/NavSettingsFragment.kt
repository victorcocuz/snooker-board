package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentNavSettingsBinding

class NavSettingsFragment : androidx.fragment.app.Fragment() {
    private val mainVm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentNavSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_settings, container, false)


        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            varMainVm = mainVm
        }
        return binding.root
    }
}