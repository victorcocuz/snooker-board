package com.example.snookerscore.fragments.rankings

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.domain.DomainRanking

@BindingAdapter("listData")
fun bindRankingsRv(recyclerView: RecyclerView, data: List<DomainRanking>?) {
    val adapter = recyclerView.adapter as RankingsAdapter
    adapter.submitList(data)
}