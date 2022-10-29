package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentNavImproveBinding
import com.quickpoint.snookerboard.utils.setAsLink

class NavImproveFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentNavImproveBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_improve, container, false)

        binding.apply {
            fNavImproveContactEmail.setAsLink()
            fNavImproveSurveyLink.setAsLink()
        }

        return binding.root
    }
}