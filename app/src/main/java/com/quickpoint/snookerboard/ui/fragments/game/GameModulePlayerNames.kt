package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.quickpoint.snookerboard.domain.objects.DomainPlayer
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.components.TextTitle
import com.quickpoint.snookerboard.ui.theme.Brown
import com.quickpoint.snookerboard.ui.theme.Transparent
import timber.log.Timber

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
    Timber.e("subtitle $textSubtitle")
    TextSubtitle(textSubtitle)
}