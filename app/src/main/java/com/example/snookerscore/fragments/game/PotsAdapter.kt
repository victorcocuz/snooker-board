package com.example.snookerscore.fragments.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBallViewSmallBinding
import com.example.snookerscore.domain.Pot

class PotsAdapter:
    ListAdapter<Pot, PotsAdapter.ViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: ItemBallViewSmallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pot) {
            binding.apply {
                ball = item.ball
                executePendingBindings()
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemBallViewSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Pot>() {
        override fun areItemsTheSame(oldItem: Pot, newItem: Pot): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pot, newItem: Pot): Boolean {
            return oldItem.ball == newItem.ball
        }
    }

}
