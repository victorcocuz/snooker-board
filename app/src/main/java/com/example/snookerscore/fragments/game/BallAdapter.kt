package com.example.snookerscore.fragments.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBallViewBinding
import com.example.snookerscore.domain.Ball
import com.example.snookerscore.domain.BallAdapterType

class BallAdapter(private val clickListener: BallListener?, private val ballStack: LiveData<MutableList<Ball>>?, private val adapterType: BallAdapterType): ListAdapter<Ball, BallAdapter.ViewHolder>(BallAdapterCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, ballStack, adapterType)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemBallViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding, parent.context)
            }
        }

        fun bind(item: Ball, clickListener: BallListener?, ballStack: LiveData<MutableList<Ball>>?, adapterType: BallAdapterType) {
            binding.apply {
                ball = item
                val factor = when (adapterType) {
                    BallAdapterType.MATCH -> 7
                    BallAdapterType.FOUL -> 11
                    BallAdapterType.BREAK -> 22
                }
                itemBallViewFrameLayout.apply {
                    layoutParams.width = context.resources.displayMetrics.widthPixels / factor
                    layoutParams.height = context.resources.displayMetrics.widthPixels / factor
                    setPadding(28 / factor, 28 / factor, 28 / factor, 28 / factor)
                }

                this.ballStackSize = ballStack?.value?.size ?: 0
                this.clickListener = clickListener
                executePendingBindings()
            }
        }
    }
}

class BallAdapterCallback : DiffUtil.ItemCallback<Ball>() {
    override fun areItemsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Ball, newItem: Ball): Boolean {
        return oldItem.points == newItem.points
    }
}

class BallListener(val clickListener: (ball: Ball) -> Unit) {
    fun onClick(ball: Ball) = clickListener(ball)
}
