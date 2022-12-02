package com.quickpoint.snookerboard.utils

import android.view.View.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.BallType.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.fragments.game.BallAdapter
import com.quickpoint.snookerboard.fragments.summary.SummaryAdapter
import com.quickpoint.snookerboard.utils.BallAdapterType.*
import com.quickpoint.snookerboard.utils.BallAdapterType.MATCH
import com.quickpoint.snookerboard.utils.MatchAction.*
import com.quickpoint.snookerboard.utils.PlayerTagType.*

// General
@BindingAdapter("setSelected")
fun TextView.setViewSelected(selected: Boolean) {
    if (isEnabled) {
        isSelected = selected
        setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.white
        else if (!isEnabled) R.color.white
        else R.color.black))
    }
}

@BindingAdapter("setVisible")
fun TextView.setVisible(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> VISIBLE
        false -> GONE
    }
}

@BindingAdapter("setFreeballVisible")
fun TextView.setFreeballVisible(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> {
            if (visibility == INVISIBLE) startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_short))
            VISIBLE
        }
        false -> GONE
    }
}

// Game Display
@BindingAdapter("setActivePlayer", "setActivePlayerTag")
fun LinearLayout.setActivePlayer(isActivePlayer: Boolean, activePlayerTag: PlayerTagType) {
    if (activePlayerTag == STATISTICS) setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
    else colorTransition(isActivePlayer, if (isActivePlayer) R.color.transparent else R.color.brown)
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

@BindingAdapter("setBallImage", "ballAdapterType")
fun ImageView.setBallImage(item: DomainBall?, ballAdapterType: BallAdapterType) {
    item?.let {
        if (ballAdapterType == MATCH) startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_short))
        val ripple = ballAdapterType != BREAK
        setBackgroundResource(when (item) {
            is RED -> if (ripple) R.drawable.ic_ball_red else R.drawable.ic_ball_red_normal
            is YELLOW -> if (ripple) R.drawable.ic_ball_yellow else R.drawable.ic_ball_yellow_normal
            is GREEN -> if (ripple) R.drawable.ic_ball_green else R.drawable.ic_ball_green_normal
            is BROWN -> if (ripple) R.drawable.ic_ball_brown else R.drawable.ic_ball_brown_normal
            is BLUE -> if (ripple) R.drawable.ic_ball_blue else R.drawable.ic_ball_blue_normal
            is PINK -> if (ripple) R.drawable.ic_ball_pink else R.drawable.ic_ball_pink_normal
            is BLACK -> if (ripple) R.drawable.ic_ball_black else R.drawable.ic_ball_black_normal
            is FREEBALL -> if (ripple) R.drawable.ic_ball_free else R.drawable.ic_ball_free_normal
            is NOBALL -> if (ripple) R.drawable.ic_ball_miss else R.drawable.ic_ball_miss_normal
            else -> if (ripple) R.drawable.ic_ball_white else R.drawable.ic_ball_white_normal
        })
    }
}

// RV Adapters
@BindingAdapter("bindMatchBallsRv")
fun RecyclerView.bindBallsRv(ballList: MutableList<DomainBall>?) {
    val adapter = adapter as BallAdapter
    adapter.submitList(when (ballList?.lastOrNull()) {
        is COLOR -> listOfBallsColors
        is WHITE -> listOf(NOBALL())
        null -> listOf()
        else -> listOf(ballList.last())
    })
}

@BindingAdapter("bindFoulBalls")
fun RecyclerView.bindFoulBalls(ballStack: MutableList<DomainBall>?) {
    val adapter = adapter as BallAdapter
    adapter.submitList(when (ballStack?.size) {
        null -> listOf()
        in (2..8) -> removeBallsForFoulDialog(ballStack)
        else -> listOfBallsPlayable
    })
}

@BindingAdapter("bindGameStatsData")
fun RecyclerView.bindGameStatsRv(data: ArrayList<Pair<DomainScore, DomainScore>>?) {
    val adapter = adapter as SummaryAdapter
    adapter.submitList(data)
}