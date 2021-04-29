package com.example.snookerscore.fragments.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBreakViewBinding
import com.example.snookerscore.domain.Break

class BreaksAdapter:
    ListAdapter<Break, BreaksAdapter.ViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: ItemBreakViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Break) {
            binding.apply {
                crtBreak = item
                executePendingBindings()
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemBreakViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Break>() {
        override fun areItemsTheSame(oldItem: Break, newItem: Break): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Break, newItem: Break): Boolean {
            return oldItem.breakSize == newItem.breakSize
        }
    }

}
