package com.quickpoint.snookerboard.fragments.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.databinding.ItemBallViewBinding
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.utils.BALL_HEIGHT_FACTOR_MATCH_ACTION
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.getFactoredDimen

class BallAdapter(
    private val clickListener: BallListener?, // provide the listener method through the adapter
    private val frame: LiveData<DomainFrame>?, // provide the frame as live data to dynamically get the stack size in order to show # of reds remaining
    private val adapterType: BallAdapterType // provide adapter type to adjust view size
) : ListAdapter<DomainBall, BallAdapter.ViewHolder>(BallAdapterCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, frame, adapterType)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder { // Returns a view holder given the parent
                val binding = ItemBallViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding, parent.context)
            }
        }

        fun bind(item: DomainBall?, clickListener: BallListener?, frame: LiveData<DomainFrame>?, adapterType: BallAdapterType) {
            binding.apply {
                ball = item
                val factor = when (adapterType) {
                    BallAdapterType.MATCH -> BALL_HEIGHT_FACTOR_MATCH_ACTION
                    BallAdapterType.FOUL -> 7
                    BallAdapterType.BREAK -> 20
                }
                val padding = when (adapterType) {
                    BallAdapterType.MATCH -> 8
                    BallAdapterType.FOUL -> 16
                    BallAdapterType.BREAK -> 4
                }
                itemBallViewFrameLayout.apply { // Adjust ball size and padding depending on the adaptor type
                    layoutParams.width = context.getFactoredDimen(factor)
                    layoutParams.height = context.getFactoredDimen(factor)
                    setPadding(padding, padding, padding, padding)
                }

                this.ballStackSize = frame?.value?.ballStack?.size ?: 0
                this.clickListener = clickListener // binds the click listener to the view holder
                executePendingBindings()
            }
        }
    }
}

class BallAdapterCallback : DiffUtil.ItemCallback<DomainBall>() {
    override fun areItemsTheSame(oldItem: DomainBall, newItem: DomainBall): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DomainBall, newItem: DomainBall): Boolean {
        return oldItem.points == newItem.points
    }
}

// Create a listener class that uses a constructor which uses a function in the constructor?
// The function requires a ball to trigger a lambda function (Unit)
// The class has an onclick method which equals to function provided through the constructor
class BallListener(val clickListener: (ball: DomainBall) -> Unit) {
    fun onClick(ball: DomainBall) = clickListener(ball)
}
