package com.example.snookerscore.fragments.game.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBallViewBinding
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.BallType

class FoulDialogAdapter(val clickListener: FoulDialogListener): ListAdapter<Ball, FoulDialogAdapter.ViewHolder>(FoulDialogAdapterCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Ball, clickListener: FoulDialogListener) {
            binding.ball = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemBallViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class FoulDialogAdapterCallback : DiffUtil.ItemCallback<Ball>() {
    override fun areItemsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem.ballType == newItem.ballType
    }

    override fun areContentsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem == newItem
    }
}

class FoulDialogListener(val clickListener: (ballType: BallType) -> Unit) {
    fun onClick(ball: Ball) = clickListener(ball.ballType)
}