package com.example.snookerscore.fragments.gamestatistics


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemGameStatisticsViewBinding
import com.example.snookerscore.domain.FrameScore

class GameStatsAdapter:
    ListAdapter<Pair<FrameScore, FrameScore>, GameStatsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: ItemGameStatisticsViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(frameScores: Pair<FrameScore, FrameScore>) {
            binding.apply {
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

    companion object DiffCallback : DiffUtil.ItemCallback<Pair<FrameScore, FrameScore>>() {
        override fun areItemsTheSame(oldItem: Pair<FrameScore, FrameScore>, newItem: Pair<FrameScore, FrameScore>): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pair<FrameScore, FrameScore>, newItem: Pair<FrameScore, FrameScore>): Boolean {
            return oldItem.first.frameCount == newItem.first.frameCount
        }
    }
}