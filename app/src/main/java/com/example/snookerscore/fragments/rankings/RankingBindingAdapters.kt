package com.example.snookerscore.fragments.rankings

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.domain.DomainRanking
import com.example.snookerscore.fragments.game.Frame
import com.example.snookerscore.fragments.gamestatistics.GameStatsAdapter
import timber.log.Timber

@BindingAdapter("listRankingsData")
fun bindRankingsRv(recyclerView: RecyclerView, data: List<DomainRanking>?) {
    val adapter = recyclerView.adapter as RankingsAdapter
    adapter.submitList(data)
}


@BindingAdapter("listGameStatsData")
fun bindGameStatsRv(recyclerView: RecyclerView, data: List<Frame>?) {
    Timber.e("test this $data")
    val adapter = recyclerView.adapter as GameStatsAdapter
    adapter.submitList(data)
}