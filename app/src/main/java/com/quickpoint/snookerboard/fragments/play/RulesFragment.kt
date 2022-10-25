package com.quickpoint.snookerboard.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentRulesBinding

class RulesFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentRulesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)

        return binding.root
    }
}