package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.RowHorizontalDivider
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.Constants
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.GenericViewModelFactory

@Composable
fun ScreenGame(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val gameVm: GameViewModel = viewModel(factory = GenericViewModelFactory(dataStore))

    val domainFrame by gameVm.frameState.collectAsState()
    var isLongSelected by remember { mutableStateOf(Toggle.LongShot.isEnabled) }
    var isRestSelected by remember { mutableStateOf(Toggle.RestShot.isEnabled) }
    LaunchedEffect(true) { // At first launch, either load or start new match
        if (Settings.matchState == MatchState.RULES_IDLE) {
            gameVm.resetMatch()
            Settings.matchState = MatchState.GAME_IN_PROGRESS
        } else mainVm.eventStoredFrame.collect { event ->
            gameVm.loadMatch(event.getContentIfNotHandled())
        }
        gameVm.eventSettingsUpdated.collect {
            isLongSelected = Toggle.LongShot.isEnabled
            isRestSelected = Toggle.RestShot.isEnabled
        }
    }

    var crtPlayer by remember { mutableStateOf(Settings.crtPlayer) }
    LaunchedEffect(domainFrame) {
        crtPlayer = Settings.crtPlayer
        isLongSelected = Toggle.LongShot.isEnabled
        isRestSelected = Toggle.RestShot.isEnabled
    }

    FragmentContent(paddingValues = PaddingValues(MaterialTheme.spacing.default)) {
        GameModulePlayerNames(crtPlayer)
        GameModuleContainer(
            title = stringResource(R.string.l_game_score_ll_line_module_score)
        ) { GameModuleScore(domainFrame) }
//        GameModuleContainer(
//            title = stringResource(R.string.l_game_score_ll_line_module_statistics)
//        ) { GameModuleStatistics(domainFrame.score) }
        GameModuleContainer(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.l_game_score_ll_line_module_breaks)
        ) { GameModuleBreaks(domainFrame.frameStack) }
        GameModuleActions(gameVm, domainFrame, isLongSelected, isRestSelected)
    }
}

@Composable // Game
fun GameModuleContainer(
    modifier: Modifier = Modifier,
    title: String = Constants.EMPTY_STRING,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier = modifier.padding(MaterialTheme.spacing.medium, 0.dp)) {
    if (title != "") ModuleTitle(title)
    content()
}

@Composable
fun ModuleTitle(
    text: String,
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 16.dp, 0.dp, 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    RowHorizontalDivider()
    TextSubtitle(text)
    RowHorizontalDivider()
}



