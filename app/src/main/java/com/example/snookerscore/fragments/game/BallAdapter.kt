package com.example.snookerscore.fragments.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBallViewBinding

class BallAdapter(private val clickListener: BallListener): ListAdapter<Pair<Ball, ShotType>, BallAdapter.ViewHolder>(BallAdapterCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<Ball, ShotType>, clickListener: BallListener) {
            binding.apply {
                ball = item.first
                shotType = item.second
                this.clickListener = clickListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemBallViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class BallAdapterCallback : DiffUtil.ItemCallback<Pair<Ball, ShotType>>() {
    override fun areItemsTheSame(oldItem: Pair<Ball, ShotType>, newItem: Pair<Ball, ShotType>): Boolean {
        return oldItem.first.ballType == newItem.first.ballType
    }

    override fun areContentsTheSame(oldItem: Pair<Ball, ShotType>, newItem: Pair<Ball, ShotType>): Boolean {
        return oldItem == newItem
    }
}

class BallListener(val clickListener: (ball: Ball, shotType: ShotType) -> Unit) {
    fun onClick(ball: Ball, shotType: ShotType) = clickListener(ball, shotType)
}