package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.styles.TextTitle
import com.quickpoint.snookerboard.compose.ui.theme.Brown
import com.quickpoint.snookerboard.compose.ui.theme.Transparent
import com.quickpoint.snookerboard.domain.objects.DomainPlayer

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
        .background(color = if (isActive) Transparent else Brown),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    TextTitle(textTitle)
    TextParagraphSubTitle(textSubtitle)
}