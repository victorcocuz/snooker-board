package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentNavAboutBinding

class NavAboutFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentNavAboutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_about, container, false)

        return binding.root
    }
}