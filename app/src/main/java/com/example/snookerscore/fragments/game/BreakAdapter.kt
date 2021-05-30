package com.example.snookerscore.fragments.game

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBreakViewBinding
import com.example.snookerscore.domain.BallAdapterType
import com.example.snookerscore.domain.DomainBreak

class BreakAdapter(private val activity: Activity) :
    ListAdapter<DomainBreak, BreakAdapter.ViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), activity)
    }

    class ViewHolder private constructor(val binding: ItemBreakViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DomainBreak, activity: Activity) {
            binding.apply {
                crtBreak = item

                itemBreakABallsRv.apply {
                    adapter = BallAdapter(null, null, BallAdapterType.BREAK)
                    layoutManager = GridLayoutManager(activity, 6)
                }

                itemBreakBBallsRv.apply {
                    adapter = BallAdapter(null, null, BallAdapterType.BREAK)
                    layoutManager = GridLayoutManager(activity, 6)
                }

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

    companion object DiffCallBack : DiffUtil.ItemCallback<DomainBreak>() {
        override fun areItemsTheSame(oldItem: DomainBreak, newItem: DomainBreak): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DomainBreak, newItem: DomainBreak): Boolean {
            return oldItem.breakSize == newItem.breakSize
        }
    }

}
