package com.quickpoint.snookerboard.fragments.gamestatistics


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.databinding.ItemGameStatisticsViewBinding
import com.quickpoint.snookerboard.domain.DomainPlayerScore

class GameStatsAdapter:
    ListAdapter<Pair<DomainPlayerScore, DomainPlayerScore>, GameStatsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder private constructor(private val binding: ItemGameStatisticsViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(frameScores: Pair<DomainPlayerScore, DomainPlayerScore>, position: Int) {
            binding.apply {
                varBgType = position % 2
                frameScoreA = frameScores.first
                frameScoreB = frameScores.second
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemGameStatisticsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Pair<DomainPlayerScore, DomainPlayerScore>>() {
        override fun areItemsTheSame(oldItem: Pair<DomainPlayerScore, DomainPlayerScore>, newItem: Pair<DomainPlayerScore, DomainPlayerScore>): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pair<DomainPlayerScore, DomainPlayerScore>, newItem: Pair<DomainPlayerScore, DomainPlayerScore>): Boolean {
            return oldItem.first.frameId == newItem.first.frameId
        }
    }
}