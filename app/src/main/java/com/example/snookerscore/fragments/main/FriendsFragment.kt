package com.example.snookerscore.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentFriendsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false)
        return binding.root
    }
}