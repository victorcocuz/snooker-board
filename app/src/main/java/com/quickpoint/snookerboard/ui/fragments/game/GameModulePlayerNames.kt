package com.quickpoint.snookerboard.ui.fragments.game

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
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.objects.DomainPlayer
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.*
import com.quickpoint.snookerboard.utils.PlayerTagType
import com.quickpoint.snookerboard.utils.colorTransition

@Composable
fun GameModulePlayerNames(crtPlayer: Int) = Row(Modifier.fillMaxWidth()) {
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