package com.example.snookerscore.fragments.rankings


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.databinding.ItemRankingViewBinding
import com.example.snookerscore.domain.DomainRanking

class RankingsAdapter:
    ListAdapter<DomainRanking, RankingsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    class ViewHolder private constructor(private val binding: ItemRankingViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ranking: DomainRanking) {
            binding.ranking = ranking
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemRankingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DomainRanking>() {
        override fun areItemsTheSame(oldItem: DomainRanking, newItem: DomainRanking): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DomainRanking, newItem: DomainRanking): Boolean {
            return oldItem.position == newItem.position
        }
    }
}