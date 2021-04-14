package com.example.snookerscore.fragments.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentRankingsBinding

class RankingsFragment : Fragment() {

    private val rankingsViewModel: RankingsFragmentViewModel by lazy {
        ViewModelProvider(this, RankingsFragmentViewModel.Factory(requireNotNull(this.activity).application)).get(RankingsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentRankingsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rankings, container, false)


//        val adapter = RankingsAdapter()
        binding.apply {
            lifecycleOwner = this@RankingsFragment
            viewModel = rankingsViewModel
            rankingsRv.adapter = RankingsAdapter()
        }

//        rankingsViewModel.rankings.observe(viewLifecycleOwner,  Observer {
//            it?.let {
//                adapter.submitList(it)
//            }
//        })
        return binding.root
    }
}