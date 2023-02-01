package com.quickpoint.snookerboard.fragments.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.databinding.ItemBallViewBinding
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.fragments.game.BallAdapter.ViewHolder.Companion.singleItemSelectionPosition
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.Constants.FACTOR_BALL_MATCH
import com.quickpoint.snookerboard.utils.getFactoredDimen

class BallAdapter(
    private val clickListener: BallListener?, // provide the listener method through the adapter
    private val frame: LiveData<DomainFrame>?, // provide the frame as live data to dynamically get the stack size in order to show # of reds remaining
    private val adapterType: BallAdapterType, // provide adapter type to adjust view size
) : ListAdapter<DomainBall, BallAdapter.ViewHolder>(BallAdapterCallback()) {

    private var oldList: List<DomainBall> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, frame, adapterType, this)
    }

    override fun submitList(list: List<DomainBall>?) {
        singleItemSelectionPosition = -1
        if (adapterType == BallAdapterType.MATCH) { // Don't submit match list if old result is the same, to avoid unwanted ball animation flicker
            if (list?.lastOrNull() != oldList.lastOrNull()) super.submitList(list)
            oldList = list ?: mutableListOf()
        } else super.submitList(list)
    }

    class ViewHolder private constructor(val binding: ItemBallViewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            var singleItemSelectionPosition = -1
            fun from(parent: ViewGroup): ViewHolder { // Returns a view holder given the parent
                val binding = ItemBallViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding, parent.context)
            }
        }

        fun bind(
            item: DomainBall?,
            clickListener: BallListener?,
            frame: LiveData<DomainFrame>?,
            adapterType: BallAdapterType,
            adapter: BallAdapter,
        ) {
            binding.apply {
                ball = item
                val factor = when (adapterType) {
                    BallAdapterType.MATCH -> FACTOR_BALL_MATCH
                    BallAdapterType.FOUL -> 7
                    BallAdapterType.BREAK -> 20
                }
                val padding = when (adapterType) {
                    BallAdapterType.MATCH -> 8
                    BallAdapterType.FOUL -> 16
                    BallAdapterType.BREAK -> 4
                }
                iBallViewFlBall.apply { // Adjust ball size and padding depending on the adaptor type
                    layoutParams.width = context.getFactoredDimen(factor)
                    layoutParams.height = context.getFactoredDimen(factor)
                    setPadding(padding)
                }

                // Set clickListener here rather than xml to apply ball selection actions
                if (adapterType == BallAdapterType.FOUL) {
                    iBallViewBtnBall.isSelected = singleItemSelectionPosition == adapterPosition
                }
                iBallViewBtnBall.setOnClickListener {
                    if (adapterType != BallAdapterType.BREAK) this.clickListener!!.onClick(ball!!)
                    if (adapterType == BallAdapterType.FOUL) adapter.setSingleSelection(adapterPosition)
                }
                this.ballAdapterType = adapterType
                this.ballStackSize = frame?.value?.ballStack?.size ?: 0
                this.clickListener = clickListener // binds the click listener to the view holder
                executePendingBindings()
            }
        }
    }

    fun setSingleSelection(adapterPosition: Int) {
        if (adapterPosition == RecyclerView.NO_POSITION) return
        notifyItemChanged(singleItemSelectionPosition)
        singleItemSelectionPosition = adapterPosition
        notifyItemChanged(singleItemSelectionPosition)
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

// Create a listener class that uses a constructor which uses a method in the constructor
// The function requires a ball to trigger a lambda function (Unit)
// The class has an onclick method which equals to method provided through the constructor
class BallListener(val clickListener: (ball: DomainBall) -> Unit) {
    fun onClick(ball: DomainBall) = clickListener(ball)
}
