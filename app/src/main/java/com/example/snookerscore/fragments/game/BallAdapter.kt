package com.example.snookerscore.fragments.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBallViewBinding

class BallAdapter(private val clickListener: BallListener, private val ballStackSize: LiveData<Int>): ListAdapter<Ball, BallAdapter.ViewHolder>(BallAdapterCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, ballStackSize)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Ball, clickListener: BallListener, ballStackSize: LiveData<Int>) {
            binding.apply {
                ball = item
                this.ballStackSize = ballStackSize.value!!
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

class BallAdapterCallback : DiffUtil.ItemCallback<Ball>() {
    override fun areItemsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem.ballType == newItem.ballType
    }

    override fun areContentsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem == newItem
    }
}

class BallListener(val clickListener: (ball: Ball) -> Unit) {
    fun onClick(ball: Ball) = clickListener(ball)
}