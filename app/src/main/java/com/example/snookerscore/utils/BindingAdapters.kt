package com.example.snookerscore.utils

import android.app.Application
import android.view.View
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
import java.text.DecimalFormat

// General
@BindingAdapter("setVisible")
fun TextView.setVisible(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}

// Game Display
@BindingAdapter("setActivePlayer")
fun TextView.setActivePlayer(activePlayer: Boolean) = setBackgroundColor(
    if (activePlayer) ContextCompat.getColor(context, R.color.design_default_color_primary)
    else 0x00000000
)

@BindingAdapter("setTotalScore")
fun TextView.setTotalScore(application: Application) {
    val frames = application.getSharedPref().getInt(application.getString(R.string.shared_pref_match_frames), 0)
    text = context.getString(R.string.game_total_score, (frames * 2 - 1))
}

@BindingAdapter("getFromPrefsNameA")
fun TextView.getFromPrefsNameA(application: Application) {
    text = application.getSharedPref().getString(application.getString(R.string.shared_pref_match_name_first_a), application.getString(R.string.you))
}

@BindingAdapter("getFromPrefsNameB")
fun TextView.getFromPrefsNameB(application: Application) {
    text = application.getSharedPref().getString(application.getString(R.string.shared_pref_match_name_first_b), application.getString(R.string.guest))
}

// Statistics Adapters
@BindingAdapter("setFrameScorePercentage")
fun TextView.setFrameScorePercentage(frameScore: DomainPlayerScore?) = frameScore?.apply {
    val df = DecimalFormat("##%")
    text = when ((successShots + missedShots)) {
        in (1..10000) -> df.format((successShots.toDouble() / (successShots.toDouble() + missedShots.toDouble())))
        -1 -> context.getString(R.string.fragment_statistics_header_percentage)
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

@BindingAdapter("setGameStatsType", "setGameStatsValue")
fun TextView.setGameStatsValue(type: StatisticsType, value: Int) {
    text = when (value) {
        -1 -> when (type) {
            StatisticsType.FRAME_ID -> context.getString(R.string.fragment_statistics_header_frame_id)
            StatisticsType.HIGHEST_BREAK -> context.getString(R.string.fragment_statistics_header_break)
            StatisticsType.FRAME_POINTS -> context.getString(R.string.fragment_statistics_header_points)
        }
        -2 -> context.getString(R.string.fragment_statistics_footer_total)
        else -> value.toString()
    }
}

// Gen Dialog Adapters
@BindingAdapter("dialogGameGenQuestion")
fun TextView.setDialogGameGenQuestion(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Are you sure you want to cancel the current match? You will lose all match progress"
        MatchAction.FRAME_END_QUERY -> "Are you sure you want to end this frame? It is still in progress."
        MatchAction.MATCH_END_QUERY -> "Are you sure you want to end this match? It is still in progress."
        MatchAction.FRAME_END_CONFIRM -> "This frame will end. Would you like to proceed?"
        MatchAction.MATCH_END_CONFIRM -> "This match will end. Would you like to proceed?"
        MatchAction.MATCH_RELOAD -> "Would you like to continue the current match or start a new one"
        MatchAction.INFO_FOUL -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenA")
fun TextView.setDialogGameA(matchAction: MatchAction) {
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "No"
        MatchAction.FRAME_END_QUERY -> "No"
        MatchAction.MATCH_END_QUERY -> "No"
        MatchAction.FRAME_END_CONFIRM -> "No"
        MatchAction.MATCH_END_CONFIRM -> "No"
        MatchAction.MATCH_RELOAD -> "Start New Match"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameGenB", "dialogGameGenBScore")
fun TextView.setDialogGameB(matchAction: MatchAction, score: CurrentScore?) {
    visibility = when {
        score == null -> View.GONE
        score.getFirst().matchPoints + score.getSecond().matchPoints == 0 -> View.GONE
        matchAction == MatchAction.MATCH_END_CONFIRM_DISCARD -> View.VISIBLE
        else -> View.GONE
    }
    text = when (matchAction) {
        MatchAction.MATCH_END_CONFIRM_DISCARD -> "End Match, Discard Frame"
        else -> ""
    }
}

@BindingAdapter("dialogGameGenC", "dialogGameGenCActionB", "dialogGameGenCScore")
fun TextView.setDialogGameC(matchAction: MatchAction, matchActionB: MatchAction, score: CurrentScore?) {
    isEnabled = !(matchActionB == MatchAction.MATCH_END_CONFIRM_DISCARD && (score?.isFrameEqual() ?: false))
    text = when (matchAction) {
        MatchAction.MATCH_CANCEL -> "Yes"
        MatchAction.FRAME_END_QUERY -> "Yes"
        MatchAction.MATCH_END_QUERY -> "Yes"
        MatchAction.FRAME_END_CONFIRM -> "Yes"
        MatchAction.MATCH_END_CONFIRM -> "Yes"
        MatchAction.MATCH_RELOAD -> "Continue Match"
        else -> "$matchAction not implemented"
    }
}

@BindingAdapter("dialogGameNote", "dialogGameNoteScore")
fun TextView.setDialogGameNote(matchAction: MatchAction, score: CurrentScore?) {
    visibility = if (matchAction == MatchAction.MATCH_END_CONFIRM_DISCARD) View.VISIBLE else View.GONE
    text = when {
        score == null -> ""
        score.getFirst().matchPoints + score.getFirst().matchPoints == 0 -> ""
        score.getWinner().matchPoints + 1 == score.getWinner().getOther().matchPoints -> "Keep frame results in draw"
        score.matchPoints == score.getOther().matchPoints -> "Discard frame results in draw"
        else -> ""
    }
}

// Break Adapters
@BindingAdapter("setBreakPoints", "setBreakPointsPlayer")
fun TextView.setBreakPoints(crtBreak: DomainBreak, player: Int) {
    text = when {
        crtBreak.player == player && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
        crtBreak.player != player && crtBreak.pots.last().potType == PotType.FOUL -> crtBreak.pots.last().ball.foul.toString()
        else -> ""
    }
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

// RV Adapters
@BindingAdapter("bindMatchBallsRv", )
fun RecyclerView.bindBallsRv(ballList: MutableList<DomainBall>?) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(
        when (ballList?.lastOrNull()) {
            is COLOR -> listOf(YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
            is WHITE -> listOf()
            else -> listOf(ballList?.lastOrNull())
        }
    )
}

@BindingAdapter("bindFoulBalls")
fun RecyclerView.bindFoulBalls(ballStack: MutableList<DomainBall>) {
    val adapter = this.adapter as BallAdapter
    adapter.submitList(
        when (ballStack.size) {
            in (2..7) -> ballStack.reversed()
            else -> listOf(WHITE(), RED(), YELLOW(), GREEN(), BROWN(), BLUE(), PINK(), BLACK())
        }
    )
}

@BindingAdapter("bindGameStatsData")
fun RecyclerView.bindGameStatsRv(data: ArrayList<Pair<DomainPlayerScore, DomainPlayerScore>>?) {
    val adapter = this.adapter as GameStatsAdapter
    adapter.submitList(data)
}

@BindingAdapter("bindBreakData")
fun RecyclerView.bindBreakRv(breaks: MutableList<DomainBreak>?) = breaks?.let {
    val adapter = this.adapter as BreakAdapter
    adapter.submitList(breaks.getDisplayShots())
}

@BindingAdapter("bindPots", "bindPotsPlayer")
fun RecyclerView.bindPotsRvB(crtBreak: DomainBreak?, player: Int) {
    val adapter = this.adapter as BallAdapter
    val balls = mutableListOf<DomainBall>()
    crtBreak?.pots?.forEach {
        balls.add(it.ball)
    }
    adapter.submitList(if (crtBreak?.player == player) balls else mutableListOf())
}