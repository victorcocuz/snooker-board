package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.ui.components.BallView
import com.quickpoint.snookerboard.ui.components.ContainerColumn
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.BrownMedium
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.Constants

@Composable
fun ColumnScope.ModuleGameBreaks(frameStack: List<DomainBreak>, isAdvancedBreaksActive: Boolean) = ContainerColumn(Modifier.weight(1f)){

    val lazyListState = rememberLazyListState()
    LaunchedEffect(frameStack.size) {
        lazyListState.animateScrollToItem(frameStack.size)
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val ballHeight = (maxWidth / 2 - MaterialTheme.spacing.breakTextInfo) / 6
        LazyColumn(
            reverseLayout = true,
            state = lazyListState
        ) {
            items(frameStack.displayShots(isAdvancedBreaksActive)) { domainBreak ->
                StandardRow(Modifier.height(ballHeight * ((domainBreak.pots.size - 1) / 6 + 1) + MaterialTheme.spacing.medium)) {
                    SingleBreak(domainBreak, 0) { domainBreak, player ->
                        if (domainBreak.player == player || domainBreak.isLastBallFoul()) {
                            BreakBalls(domainBreak, ballHeight, player)
                            BreakInfo(domainBreak, player)
                            BreakPoints(domainBreak, player)
                        }
                    }
                    SingleBreak(domainBreak, 1) { domainBreak, player ->
                        if (domainBreak.player == player || domainBreak.isLastBallFoul()) {
                            BreakPoints(domainBreak, player)
                            BreakBalls(domainBreak, ballHeight, player)
                            BreakInfo(domainBreak, player)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SingleBreak(
    domainBreak: DomainBreak,
    player: Int,
    content: @Composable (DomainBreak, Int) -> Unit,
) = StandardRow(
    Modifier
        .weight(1f)
        .padding(MaterialTheme.spacing.extraSmall)
        .fillMaxHeight()
        .clip(RoundedCornerShape(MaterialTheme.spacing.extraSmall))
        .background(if (domainBreak.player == player || domainBreak.isLastBallFoul()) BrownDark else Color.Transparent)
        .border(
            MaterialTheme.spacing.border,
            if (domainBreak.player == player || domainBreak.isLastBallFoul()) BrownDark else Color.Transparent
        )
) { content(domainBreak, player) }

@Composable
fun RowScope.BreakBalls(domainBreak: DomainBreak, ballHeight: Dp, player: Int) {
    val ballsList = when {
        (domainBreak.breakSize > 0) && domainBreak.player == player -> domainBreak.ballsList(player)
        (domainBreak.isLastBallFoul()) && domainBreak.player != player -> domainBreak.ballsList(1 - player)
        else -> emptyList()
    }

    if (ballsList.isNotEmpty())
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(BrownMedium)
                .padding(MaterialTheme.spacing.borderDouble, MaterialTheme.spacing.default),
            verticalArrangement = Arrangement.Center,
            columns = GridCells.Fixed(6),
            content = {
                items(ballsList) { ball ->
                    BallView(
                        modifier = Modifier.size(ballHeight),
                        ball = ball,
                        ballAdapterType = BallAdapterType.BREAK
                    )
                }
            })
}

@Composable
fun BreakPoints(domainBreak: DomainBreak, player: Int) {
    TextParagraph(
        modifier = Modifier.width(MaterialTheme.spacing.breakTextInfo),
        text = setBreakPoints(domainBreak, player),
        textAlign = TextAlign.Center
    )
}

@Composable
fun RowScope.BreakInfo(domainBreak: DomainBreak, player: Int) {
    if (domainBreak.breakSize == 0 && domainBreak.player == player)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(BrownMedium),
            contentAlignment = Alignment.Center,
        ) { TextParagraph(setBreakInfo(domainBreak, player)) }
}

fun setBreakPoints(crtBreak: DomainBreak, player: Int) = when {
    crtBreak.player == player && crtBreak.breakSize != 0 -> crtBreak.breakSize.toString()
    crtBreak.player != player && crtBreak.isLastBallFoul() -> crtBreak.lastBall()?.foul.toString()
    else -> "0"
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