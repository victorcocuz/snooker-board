package com.quickpoint.snookerboard.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.fragments.game.BallAdapter
import com.quickpoint.snookerboard.fragments.game.BreakAdapter
import com.quickpoint.snookerboard.fragments.postgame.PostGameAdapter
import com.quickpoint.snookerboard.utils.MatchAction.*

// General
@BindingAdapter("setSelected")
fun TextView.setViewSelected(selected: Boolean) {
    isSelected = selected
    setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.white
    else if (!isEnabled) R.color.white
    else R.color.black))
}

@BindingAdapter("setVisible")
fun TextView.setVisible(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}

@BindingAdapter("setFreeballVisible")
fun TextView.setFreeballVisible(isVisible: Boolean) {
    when (isVisible) {
        true -> Handler(Looper.getMainLooper()).postDelayed({
            visibility = View.VISIBLE
        }, 50)
        false -> visibility = View.GONE
    }
}

// Game Display
@BindingAdapter("setActivePlayer", "setActivePlayerTag")
fun LinearLayout.setActivePlayer(activePlayer: Boolean, activePlayerTag: PlayerTagType) {
    setBackgroundColor(ContextCompat.getColor(context,
        if (activePlayer || activePlayerTag == PlayerTagType.STATISTICS) R.color.transparent else R.color.brown))
    for (i in 0 until childCount) {
        getChildAt(i).alpha = if (activePlayer || activePlayerTag == PlayerTagType.STATISTICS) 1F else 0.5F
    }
}

@BindingAdapter("setBarWeightScoreFirst", "setBarWeightScoreSecond")
fun LinearLayout.setBarWeight(scoreFirst: Int, scoreSecond: Int) {
    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, scoreFirst.toFloat() / scoreSecond.toFloat())
}



// Ball Item View
@BindingAdapter("ballValue", "stackSize")
fun TextView.setPointsValue(item: DomainBall?, stackSize: Int?) = stackSize?.let {
    val reds = (stackSize - 7) / 2
    text = if (reds > 0 && item is RED) reds.toString() else ""
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: DomainBall?) {
    item?.let {
        setBackgroundResource(when (item) {
            is RED -> R.drawable.ic_ball_red
            is YELLOW -> R.drawable.ic_ball_yellow
            is GREEN -> R.drawable.ic_ball_green
            is BROWN -> R.drawable.ic_ball_brown
            is BLUE -> R.drawable.ic_ball_blue
            is PINK -> R.drawable.ic_ball_pink
            is BLACK -> R.drawable.ic_ball_black
            is FREEBALL -> R.drawable.ic_ball_free
            is NOBALL -> R.drawable.ic_ball_noball
            else -> R.drawable.ic_ball_white
        })
    }
}

// RV Adapters
@BindingAdapter("bindMatchBallsRv")
fun RecyclerView.bindBallsRv(ballList: MutableList<DomainBall>?) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(when (ballList?.lastOrNull()) {
        is COLOR -> listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
        is WHITE -> listOf(NOBALL())
        null -> listOf()
        else -> listOf(ballList.last())
    })
}

@BindingAdapter("bindFoulBalls")
fun RecyclerView.bindFoulBalls(ballStack: MutableList<DomainBall>) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(when (ballStack.size) {
        in (2..7) -> ballStack.reversed()
        else -> listOf(WHITE(), RED(), YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
    })
}

@BindingAdapter("bindGameStatsData")
fun RecyclerView.bindGameStatsRv(data: ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>?) {
    val adapter = this.adapter as PostGameAdapter
    adapter.submitList(data)
}

@BindingAdapter("bindBreakData")
fun RecyclerView.bindBreakRv(breaks: MutableList<DomainBreak>?) = breaks?.let {
    val adapter = this.adapter as BreakAdapter
    adapter.submitList(breaks.getDisplayShots())
}