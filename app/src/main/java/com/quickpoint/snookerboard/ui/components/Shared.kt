package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.objects.DomainPlayer
import com.quickpoint.snookerboard.ui.theme.*
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.PlayerTagType
import com.quickpoint.snookerboard.utils.colorTransition

@Composable
fun ComponentPlayerNames(crtPlayer: Int) = ContainerRow(Modifier.fillMaxWidth().padding(0.dp, 0.dp, 0.dp, 8.dp)){
    PlayerNameBox(
        textTitle = DomainPlayer.Player01.firstName,
        textSubtitle = DomainPlayer.Player01.lastName,
        isActive = crtPlayer == 0
    )
    PlayerNameBox(
        textTitle = DomainPlayer.Player02.firstName,
        textSubtitle = DomainPlayer.Player02.lastName,
        isActive = crtPlayer == 1
    )
}

@Composable
fun RowScope.PlayerNameBox(
    textTitle: String,
    textSubtitle: String,
    isActive: Boolean,
) = Column(
    modifier = Modifier
        .weight(1f)
        .padding(4.dp, 0.dp)
        .clip(RoundedCornerShape(MaterialTheme.spacing.small))
        .border(border = BorderStroke(1.dp, if (isActive) Transparent else BrownDark))
        .background(color = if (isActive) Transparent else BrownMedium)
        .padding(0.dp, 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    TextSubtitle(textTitle, color = Beige)
    TextSubtitle(textSubtitle, color = Beige)
}

fun setActivePlayer(isActivePlayer: Boolean, activePlayerTag: PlayerTagType) {
    //todo: Add color transition when changing players
//    if (activePlayerTag == STATISTICS) setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
//    else
    colorTransition(isActivePlayer, if (isActivePlayer) R.color.transparent else R.color.brown)
}

@Composable
fun RowScope.GameButtonsBalls(
    ballsList: List<DomainBall>,
    ballSize: Dp,
    ballAdapterType: BallAdapterType = BallAdapterType.MATCH,
    selectionPosition: Long = -1,
    onClick: (PotType, DomainBall) -> Unit = { _: PotType, _: DomainBall -> },
) = StandardLazyRow(Modifier.weight(1f),
    lazyItems = if (ballAdapterType == BallAdapterType.MATCH) ballsList.bindMatchBalls() else ballsList.bindFoulBalls(),
    key = { profile -> profile.ballId }
) { profile ->
    BallView(
        modifier = Modifier.size(ballSize),
        profile,
        ballAdapterType,
        isBallSelected = selectionPosition == profile.ballId,
        text = if (ballAdapterType == BallAdapterType.MATCH && profile is DomainBall.RED) ballsList.redsRemaining().toString() else ""
    ) { onClick(PotType.TYPE_HIT, profile) }
}