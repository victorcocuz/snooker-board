package com.example.snookerscore.fragments.game

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemBreakViewBinding
import com.example.snookerscore.domain.Break
import com.example.snookerscore.domain.PotType

class BreakAdapter(private val activity: Activity) :
    ListAdapter<Break, BreakAdapter.ViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), activity)
    }

    class ViewHolder private constructor(val binding: ItemBreakViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Break, activity: Activity) {
            binding.apply {
                crtBreak = item

                if (item.pots.last().potType == PotType.FOUL) {
                    if (item.player == 0) {
                        itemBreakAFouls.visibility = View.VISIBLE
                        itemBreakABallsRv.visibility = View.INVISIBLE
                    } else {
                        itemBreakBFouls.visibility = View.VISIBLE
                        itemBreakBBallsRv.visibility = View.INVISIBLE
                    }
                } else {
                    itemBreakAFouls.visibility = View.INVISIBLE
                    itemBreakABallsRv.visibility = View.VISIBLE
                    itemBreakBFouls.visibility = View.INVISIBLE
                    itemBreakBBallsRv.visibility = View.VISIBLE
                }

                itemBreakABallsRv.adapter = PotsAdapter()
                itemBreakABallsRv.layoutManager = GridLayoutManager(activity, 8)
                itemBreakBBallsRv.adapter = PotsAdapter()
                itemBreakBBallsRv.layoutManager = GridLayoutManager(activity, 8)
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
