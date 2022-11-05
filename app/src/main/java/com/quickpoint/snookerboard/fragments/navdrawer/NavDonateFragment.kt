package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.billing.Billing
import com.quickpoint.snookerboard.databinding.FragmentNavDonateBinding
import kotlinx.coroutines.launch

class NavDonateFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentNavDonateBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_donate, container, false)

        // Billing
        lifecycleScope.launch {
            Billing.processPurchases(requireActivity(), binding)
        }

        return binding.root
    }
}