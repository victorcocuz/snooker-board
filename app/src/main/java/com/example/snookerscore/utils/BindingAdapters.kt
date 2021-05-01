package com.example.snookerscore.utils

import android.app.Application
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snookerscore.R
import com.example.snookerscore.domain.*
import com.example.snookerscore.domain.DomainBall.*
import com.example.snookerscore.fragments.game.BallAdapter
import com.example.snookerscore.fragments.game.BreakAdapter
import com.example.snookerscore.fragments.game.getDisplayShots
import com.example.snookerscore.fragments.gamestatistics.GameStatsAdapter
import com.example.snookerscore.fragments.rankings.RankingsAdapter
import java.text.DecimalFormat
import kotlin.math.abs

// Ball Item View
@BindingAdapter("ballValue", "stackSize")
fun TextView.setPointsValue(item: DomainBall, stackSize: Int) {
    val reds = (stackSize - 7) / 2
    text = if (reds > 0 && item is RED) reds.toString() else ""
}

@BindingAdapter("ballImage")
fun ImageView.setBallImage(item: DomainBall?) {
    item?.let {
        setBackgroundResource(
            when (item) {
                is RED -> R.drawable.ball_red
                is YELLOW -> R.drawable.ball_yellow
                is GREEN -> R.drawable.ball_green
                is BROWN -> R.drawable.ball_brown
                is BLUE -> R.drawable.ball_blue
                is PINK -> R.drawable.ball_pink
                is BLACK -> R.drawable.ball_black
                is FREEBALL -> R.drawable.ball_grey
                else -> R.drawable.ball_white
            }
        )
    }
}

// Game Display
@BindingAdapter("crtPlayerA")
fun TextView.setCurrentPlayerA(crtPlayer: CurrentScore?) {
    crtPlayer?.let {
        setBackgroundColor(
            when (crtPlayer) {
                crtPlayer.getFirst() -> ContextCompat.getColor(context, R.color.design_default_color_primary)
                else -> 0x00000000
            }
        )
    }
}

@BindingAdapter("crtPlayerB")
fun TextView.setCurrentPlayerB(crtPlayer: CurrentScore?) {
    crtPlayer?.let {
        setBackgroundColor(
            when (crtPlayer) {
                crtPlayer.getFirst() -> 0x00000000
                else -> ContextCompat.getColor(context, R.color.design_default_color_primary)
            }
        )
    }
}

@BindingAdapter("setTotalScore")
fun TextView.setTotalScore(application: Application) {
    val frames = application.getSharedPreferences(
        application.applicationContext.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    ).getInt(application.getString(R.string.shared_pref_match_frames), 0)
    text = context.getString(R.string.game_total_score, (frames * 2 - 1))
}

@BindingAdapter("gamePointsDiffSize")
fun TextView.setGamePointsRemaining(size: Int) {
    text = if (size <= 7) ((-(8 - size) * (8 - size) - (8 - size) + 56) / 2).toString()
    else (27 + ((size - 7) / 2) * 8).toString()
}

@BindingAdapter("gamePointsRemaining")
fun TextView.setGamePointsDiff(players: MutableList<DomainPlayerScore>?) {
    players?.let {
        text = abs(it[0].framePoints - it[1].framePoints).toString()
    }
}

@BindingAdapter("shotSuccess", "shotMiss")
fun TextView.setShotPercentage(success: Double, miss: Double) {
    val df = DecimalFormat("##%")
    text = when (success + miss) {
        in (0.1..10000.0) -> df.format((success / (success + miss)))
        -2.0 -> "%"
        else -> "N/A"
    }
}

@BindingAdapter("matchPointsPlayerA", "matchPointsPlayerB")
fun TextView.setMatchPoints(matchPointsPlayerA: Int, matchPointsPlayerB: Int) {
    text = when (matchPointsPlayerA) {
        -1 -> context.getString(R.string.fragment_statistics_header_score)
        else -> context.getString(R.string.game_match_score, matchPointsPlayerA, matchPointsPlayerB)
    }
}

// Game Statistics Fragment
@BindingAdapter("gameStatsFrameNumber")
fun TextView.bindGameStatsFrameNumber(frameCount: Int) {
    text = when (frameCount) {
        -1 -> "#"
        -2 -> context.getString(R.string.fragment_statistics_footer_total)
        else -> frameCount.toString()
    }
}

@BindingAdapter("gameStatsBreak")
fun TextView.bindGameStatsBreak(highestBreak: Int) {
    text = when (highestBreak) {
        -1 -> context.getString(R.string.fragment_statistics_header_break)
        else -> highestBreak.toString()
    }
}

@BindingAdapter("gameStatsPoints")
fun TextView.bindGameStatsPoints(framePoints: Int) {
    text = when (framePoints) {
        -1 -> context.getString(R.string.fragment_statistics_header_points)
        else -> framePoints.toString()
    }
}

// Game Actions
@BindingAdapter("setMissSafeFoulEnabled")
fun TextView.setMissSafeFoulEnabled(size: Int) {
    isEnabled = when (size) {
        in (0..1) -> false
        else -> true
    }
}

// Game Foul Dialog
@BindingAdapter("dialogSetRedEnabled")
fun TextView.dialogSetRedEnabled(size: Int) {
    isEnabled = when (size) {
        in 0..8 -> false
        else -> true
    }
}

@BindingAdapter("dialogFreeBallEnabledAction")
fun TextView.setDialogFreeballEnabled(potAction: PotAction?) {
    isEnabled = when (potAction) {
        PotAction.SWITCH -> true
        else -> false
    }
}

@BindingAdapter("dialogCannotForce")
fun TextView.setDialogForceContinueEnabled(frame: DomainFrame?) {
    frame?.let {
        val diff = abs(it.frameScore[0].framePoints - it.frameScore[1].framePoints)
        val size = frame.ballStack.size
        val remaining =
            if (size <= 7) (-(8 - size) * (8 - size) - (8 - size) + 56) / 2
            else 27 + ((size - 7) / 2) * 8
        isEnabled = (remaining - diff) >= 0
    }
}

// Game Gen Dialog
@BindingAdapter("dialogGameGenQuestion")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Are you sure you want to cancel the current match? You will lose all match progress"
        MatchAction.FRAME_END_QUERY -> "Are you sure you want to end this frame?"
        MatchAction.MATCH_END_QUERY -> "Are you sure you want to end this match?"
        MatchAction.FRAME_END_CONFIRM -> "This frame will end. Would you like to proceed?"
        MatchAction.MATCH_END_CONFIRM -> "This match will end. Would you like to proceed?"
        MatchAction.MATCH_CONTINUE -> "Would you like to continue the current match or start a new one"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenYes")
fun TextView.setDialogGameYes(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Yes"
        MatchAction.FRAME_END_QUERY -> "Yes"
        MatchAction.MATCH_END_QUERY -> "Yes"
        MatchAction.FRAME_END_CONFIRM -> "Yes"
        MatchAction.MATCH_END_CONFIRM -> "Yes"
        MatchAction.MATCH_CONTINUE -> "Continue Match"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenNo")
fun TextView.setDialogGameNo(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "No"
        MatchAction.FRAME_END_QUERY -> "No"
        MatchAction.MATCH_END_QUERY -> "No"
        MatchAction.FRAME_END_CONFIRM -> "No"
        MatchAction.MATCH_END_CONFIRM -> "No"
        MatchAction.MATCH_CONTINUE -> "Start New Match"
        else -> "$matchAction not implemented"
    }
}

// Break Adapters
@BindingAdapter("crtBreakPointsA")
fun TextView.bindBreakPointsA(crtBreak: DomainBreak) {
    text = when {
        crtBreak.player == 0 && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
        crtBreak.player == 1 && crtBreak.pots.last().potType == PotType.FOUL -> crtBreak.pots.last().ball.foul.toString()
        else -> ""
    }
}

@BindingAdapter("crtBreakPointsB")
fun TextView.bindBreakPointsB(crtBreak: DomainBreak) {
    text = when {
        crtBreak.player == 1 && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
        crtBreak.player == 0 && crtBreak.pots.last().potType == PotType.FOUL -> crtBreak.pots.last().ball.foul.toString()
        else -> ""
    }
}

// RV Adapters
@BindingAdapter("listRankingsData")
fun bindRankingsRv(recyclerView: RecyclerView, data: List<DomainRanking>?) {
    val adapter = recyclerView.adapter as RankingsAdapter
    adapter.submitList(data)
}

@BindingAdapter("listGameStatsData")
fun bindGameStatsRv(recyclerView: RecyclerView, data: ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>?) {
    val adapter = recyclerView.adapter as GameStatsAdapter
    adapter.submitList(data)
}

@BindingAdapter("listBreakData")
fun bindBreakRv(recyclerView: RecyclerView, breaks: MutableList<DomainBreak>?) {
    val adapter = recyclerView.adapter as BreakAdapter
    adapter.submitList(breaks?.getDisplayShots())
}

@BindingAdapter("listPotsA")
fun RecyclerView.bindPotsRvA(crtBreak: DomainBreak?) {
    val adapter = this.adapter as BallAdapter
    val balls = mutableListOf<DomainBall>()
    crtBreak?.pots?.forEach {
        balls.add(it.ball)
    }
    adapter.submitList(if (crtBreak?.player == 0) balls else mutableListOf())
}

@BindingAdapter("listPotsB")
fun RecyclerView.bindPotsRvB(crtBreak: DomainBreak?) {
    val adapter = this.adapter as BallAdapter
    val balls = mutableListOf<DomainBall>()
    crtBreak?.pots?.forEach {
        balls.add(it.ball)
    }
    adapter.submitList(if (crtBreak?.player == 1) balls else mutableListOf())
}