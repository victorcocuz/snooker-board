package com.quickpoint.snookerboard.utils

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.fragments.game.BallAdapter

@BindingAdapter("setBreakVisibilityBreak", "setBreakVisibilityPlayer")
fun LinearLayout.setBreakVisibility(crtBreak: DomainBreak, player: Int) {
    visibility = when {
        crtBreak.lastPotType() == TYPE_FOUL -> View.VISIBLE
        crtBreak.player == player -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

@BindingAdapter("setBreakInfo", "setBreakInfoPlayer")
fun TextView.setBreakInfo(crtBreak: DomainBreak, player: Int) {
    text = if (crtBreak.player == player) when (crtBreak.lastPotType()) {
        TYPE_MISS -> "Miss"
        TYPE_SAFE -> "Success safe"
        TYPE_SAFE_MISS -> "Safe miss"
        TYPE_SNOOKER -> "Snooker"
        TYPE_REMOVERED -> "Remove red"
        TYPE_FOUL -> "Foul"
        else -> ""
    } else ""
    visibility = if (text != "") View.VISIBLE else View.GONE
}

@BindingAdapter("setBreakPoints", "setBreakPointsPlayer")
fun TextView.setBreakPoints(crtBreak: DomainBreak, player: Int) {
    text = when {
        crtBreak.player == player && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
        crtBreak.player != player && crtBreak.lastPotType() == TYPE_FOUL -> crtBreak.lastBall()?.foul.toString()
        else -> ""
    }
    visibility = if (text != "") View.VISIBLE else View.GONE
}

@BindingAdapter("bindPots", "bindPotsPlayer")
fun RecyclerView.bindPotsRv(crtBreak: DomainBreak?, player: Int) {
    val adapter = this.adapter as BallAdapter
    val balls = mutableListOf<DomainBall>()
    crtBreak?.pots?.forEach { if (it.potType in listOfPointGeneratingPotTypes) balls.add(it.ball) }
    adapter.submitList(if (crtBreak?.player == player) balls else mutableListOf())
    visibility = if (adapter.itemCount > 0 || crtBreak?.pots?.lastOrNull()?.potType == TYPE_FOUL) View.VISIBLE else View.GONE
}