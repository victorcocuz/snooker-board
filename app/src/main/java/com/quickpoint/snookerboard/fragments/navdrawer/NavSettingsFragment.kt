package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.quickpoint.snookerboard.MatchViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentNavSettingsBinding
import com.quickpoint.snookerboard.utils.MatchToggle.TOGGLE
import com.quickpoint.snookerboard.utils.vibrateOnce

class NavSettingsFragment : androidx.fragment.app.Fragment() {
    private val matchVm: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentNavSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_settings, container, false)


        binding.apply {
            fNavSettingsLToggleRules.apply {
                lNavSettingsToggleTvTitle.text = getString(R.string.f_nav_settings_toggle_rules_advanced_title)
                lNavSettingsToggleTvDescription.text = getString(R.string.f_nav_settings_toggle_rules_advanced_description)
                lNavSettingsToggleScSlider.isChecked = matchVm.matchToggle.value!!.toggleAdvancedRulesOn()
                matchVm.matchToggle.observe(viewLifecycleOwner) { toggle ->
                    lNavSettingsToggleScSlider.isChecked = toggle.toggleAdvancedRulesOn()
                }
                lNavSettingsLlRulesAdvanced.setOnClickListener {
                    TOGGLE.switchToggleAdvancedRules()
                    context?.vibrateOnce()
                    matchVm.updateMatchToggle()
                }
            }
        }
        return binding.root
    }
}