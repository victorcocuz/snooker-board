package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.styles.TextTitle
import com.quickpoint.snookerboard.compose.ui.theme.Beige
import com.quickpoint.snookerboard.compose.ui.theme.Brown
import com.quickpoint.snookerboard.compose.ui.theme.Transparent
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.domain.objects.DomainPlayer
import com.quickpoint.snookerboard.domain.objects.MatchSettings
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.GenericViewModelFactory

@Composable
fun FragmentGame(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val gameVm: GameViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    LaunchedEffect(key1 = true) {
//        gameVm.eventSharedFlow.collect { event ->
//            when (event) {
//                is ScreenEvents.SnookerEvent -> {
//                    mainVm.onEmit(event.action)
//                }
//
//                else -> {} // Not Implemented
//            }
//        }
    }
    FragmentColumn {
        Row(Modifier.fillMaxWidth()) {
            PlayerNameBox(textTitle = DomainPlayer.Player01.firstName, textSubtitle = DomainPlayer.Player01.lastName, isActive = MatchSettings.Settings.crtPlayer == 0)
            PlayerNameBox(textTitle = DomainPlayer.Player02.firstName, textSubtitle = DomainPlayer.Player02.lastName, isActive = MatchSettings.Settings.crtPlayer == 1)
        }
        GameModule(
            title = stringResource(R.string.l_game_score_ll_line_chapter_score)
        ) { GameModuleScore() }
        GameModule(
            title = stringResource(R.string.l_game_score_ll_line_chapter_statistics)
        ) { GameModuleStatistics(gameVm) }
        GameModule(modifier = Modifier.weight(1f).background(Color.Red),
            title = stringResource(R.string.l_game_score_ll_line_chapter_breakdown)
        ) { GameModuleBreaks() }
        GameModuleActions(gameVm)
    }
}

@Composable // Name Chapter
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

@Composable // Game
fun GameModule(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier = modifier) {
    GameModuleDividerLine(title)
    content()
}

@Composable
fun GameModuleDividerLine(
    text: String,
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 16.dp, 0.dp, 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    HorizontalDivider()
    TextParagraphSubTitle(text)
    HorizontalDivider()
}


@Composable
fun RowScope.HorizontalDivider() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .padding(MaterialTheme.spacing.extraSmall),
        color = Beige, thickness = 1.dp
    )
}



