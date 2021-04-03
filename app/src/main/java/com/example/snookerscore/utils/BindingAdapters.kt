package com.example.snookerscore.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.snookerscore.R
import com.example.snookerscore.fragments.game.Ball
import com.example.snookerscore.fragments.game.Balls

@BindingAdapter("ballValue")
fun TextView.setPointsValue(item: Ball?) {
    item?.let {
        text = item.points.toString()
    }
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: Ball?) {
    item?.let {
        setBackgroundResource(
            when (item) {
                Balls.RED -> R.drawable.ball_red
                Balls.YELLOW -> R.drawable.ball_yellow
                Balls.GREEN -> R.drawable.ball_green
                Balls.BROWN -> R.drawable.ball_brown
                Balls.BLUE -> R.drawable.ball_blue
                Balls.PINK -> R.drawable.ball_pink
                Balls.BLACK -> R.drawable.ball_black
                else -> R.drawable.ball_white
            }
        )
    }
}
