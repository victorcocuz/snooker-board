package com.example.snookerscore.fragments.gamestatistics


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemGameStatisticsViewBinding
import com.example.snookerscore.fragments.game.Frame

class GameStatsAdapter:
    ListAdapter<Frame, GameStatsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    class ViewHolder private constructor(private val binding: ItemGameStatisticsViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(frame: Frame) {
            binding.frame = frame
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemGameStatisticsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Frame>() {
        override fun areItemsTheSame(oldItem: Frame, newItem: Frame): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Frame, newItem: Frame): Boolean {
            return oldItem.frameCount == newItem.frameCount
        }
    }
}