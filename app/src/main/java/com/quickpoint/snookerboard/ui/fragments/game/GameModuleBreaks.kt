package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.domain.DomainBreak
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.ballsList
import com.quickpoint.snookerboard.domain.displayShots
import com.quickpoint.snookerboard.ui.components.BallView
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.Constants
import com.quickpoint.snookerboard.utils.setBallBackground

@Composable
fun GameModuleBreaks(frameStack: List<DomainBreak>) {
    val textWidth = 40.dp

    StandardRow {
        BreakColumn(frameStack, textWidth, 0) { domainBreak, ballHeight, player ->
            BreakBalls(domainBreak, ballHeight, player)
            BreakPoints(domainBreak, textWidth, player)
            BreakInfo(domainBreak, player)
        }
        BreakColumn(frameStack, textWidth, 1) { domainBreak, ballHeight, player ->
            BreakPoints(domainBreak, textWidth, player)
            BreakBalls(domainBreak, ballHeight, player)
            BreakInfo(domainBreak, player)
        }
    }
}

@Composable
fun RowScope.BreakColumn(
    frameStack: List<DomainBreak>,
    textWidth: Dp,
    player: Int,
    content: @Composable RowScope.(DomainBreak, Dp, Int) -> Unit,
) {
    BoxWithConstraints(
        Modifier
            .weight(1f)
            .fillMaxHeight()
    ) {
        val boxWidth = maxWidth
        val ballHeight = (boxWidth - textWidth) / 6
        LazyColumn {
            items(frameStack.displayShots()) { domainBreak ->
                val isCrtPlayer = domainBreak.player == player
                val factor = (domainBreak.pots.size - 1) / 6 + 1
                StandardRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.extraSmall)
                        .border(
                            shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
                            border = BorderStroke(1.dp, if (isCrtPlayer) Beige else Color.Transparent)
                        )
                        .height(ballHeight * factor)
                        .background(if (isCrtPlayer) BrownDark else Color.Transparent),
                ) { if (isCrtPlayer) content(domainBreak, ballHeight, player) }
            }
        }
    }
}

@Composable
fun RowScope.BreakBalls(domainBreak: DomainBreak, ballHeight: Dp, player: Int) {
    if (domainBreak.breakSize > 0 && domainBreak.player == player)
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            columns = GridCells.Fixed(6),
            content = {
                items(domainBreak.ballsList(player)) { ball ->
                    BallView(modifier = Modifier
                        .size(ballHeight)
                        .padding(2.dp),
                        onContent = { it.setBallBackground(ball, BallAdapterType.BREAK) })
                }
            })
}

@Composable
fun BreakPoints(domainBreak: DomainBreak, textWidth: Dp, player: Int) {
    val points = setBreakPoints(domainBreak, player)
    if (points != Constants.EMPTY_STRING) TextParagraph(
        modifier = Modifier.width(textWidth),
        text = points,
        textAlign = TextAlign.Center
    )
}

@Composable
fun BreakInfo(domainBreak: DomainBreak, player: Int) {
    val info = setBreakInfo(domainBreak, player)
    if (info != Constants.EMPTY_STRING) TextParagraph(text = info)
}

//fun RecyclerView.bindPotsRv(crtBreak: DomainBreak?, player: Int) {
//    val adapter = this.adapter as BallAdapter
//    val balls = mutableListOf<DomainBall>()
//    crtBreak?.pots?.forEach { if (it.potType in listOfPotTypesPointGenerating) balls.add(it.ball) }
//    adapter.submitList(if (crtBreak?.player == player) balls else mutableListOf())
//    visibility = if (adapter.itemCount > 0 || crtBreak?.pots?.lastOrNull()?.potType == PotType.TYPE_FOUL) View.VISIBLE else View.GONE
//}


fun setBreakPoints(crtBreak: DomainBreak, player: Int) = when {
    crtBreak.player == player && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
    crtBreak.player != player && crtBreak.lastPotType() == PotType.TYPE_FOUL -> crtBreak.lastBall()?.foul.toString()
    else -> Constants.EMPTY_STRING
}

fun setBreakInfo(crtBreak: DomainBreak, player: Int) = if (crtBreak.player == player) when (crtBreak.lastPotType()) {
    PotType.TYPE_MISS -> "Miss"
    PotType.TYPE_SAFE -> "Safe"
    PotType.TYPE_SAFE_MISS -> "Safe miss"
    PotType.TYPE_SNOOKER -> "Snooker"
    PotType.TYPE_REMOVE_RED -> "Remove red"
    PotType.TYPE_REMOVE_COLOR -> "Remove color"
    PotType.TYPE_FOUL -> "Foul"
    else -> Constants.EMPTY_STRING
} else Constants.EMPTY_STRING