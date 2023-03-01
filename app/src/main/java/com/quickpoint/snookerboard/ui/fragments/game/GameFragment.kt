package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.admob.showInterstitialAd
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.isMatchEnding
import com.quickpoint.snookerboard.domain.isMatchInProgress
import com.quickpoint.snookerboard.domain.isNoFrameFinished
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings.crtPlayer
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.ui.components.BackPressHandler
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.RowHorizontalDivider
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogFoul
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.ui.navigation.Screen
import com.quickpoint.snookerboard.ui.navigation.getActionItems
import com.quickpoint.snookerboard.ui.navigation.getActionItemsOverflow
import com.quickpoint.snookerboard.ui.theme.Transparent
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

@Composable
fun ScreenGame(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val gameVm: GameViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val dialogVm: DialogViewModel = viewModel(factory = GenericViewModelFactory())
    val domainFrame by gameVm.frameState.collectAsState()
    val context = LocalContext.current

    mainVm.setupActionBarActions(
        gameVm.getActionItems(),
        gameVm.getActionItemsOverflow()
    ) { gameVm.onMenuItemSelected(it) }

    LaunchedEffect(Unit) {
        if (Settings.matchState == MatchState.RULES_IDLE) {
            gameVm.resetMatch()
            Settings.matchState = MatchState.GAME_IN_PROGRESS
        } else gameVm.loadMatch(mainVm.cachedFrame)

//        mainVm.turnOffSplashScreen()

        gameVm.eventAction.collect { action ->
            when (action) {
                // Directly observed from gameVm
                FRAME_UPDATED -> Timber.i(context.getString(R.string.helper_update_frame_info))
                SNACK_UNDO, SNACK_ADD_RED, SNACK_REMOVE_COLOR, SNACK_FRAME_RERACK_DIALOG,
                SNACK_FRAME_ENDING_DIALOG, SNACK_MATCH_ENDING_DIALOG, SNACK_NO_BALL,
                -> mainVm.onEmit(ScreenEvents.SnackEvent(action))

                // Concerning dialogs
                FOUL_DIALOG -> dialogVm.onOpenFoulDialog()
                FOUL_CONFIRM -> {
                    gameVm.assignPot(PotType.TYPE_FOUL, dialogVm.ballClicked.value!!, dialogVm.actionClicked.value) // Temp Pot Action
                    dialogVm.onDismissFoulDialog()
                }
                FRAME_LOG_ACTIONS_DIALOG, FRAME_LAST_BLACK_FOULED_DIALOG, FRAME_RESPOT_BLACK_DIALOG, FRAME_RERACK_DIALOG, FRAME_ENDING_DIALOG, MATCH_ENDING_DIALOG,
                MATCH_CANCEL_DIALOG, FRAME_MISS_FORFEIT_DIALOG,
                -> {
                    val actions =
                        action.getListOfDialogActions(
                            gameVm.score.isMatchEnding(),
                            gameVm.score.isNoFrameFinished(),
                            gameVm.isFrameMathematicallyOver()
                        )
                    dialogVm.onOpenGenericDialog(actions)
                }
                FRAME_LOG_ACTIONS -> gameVm.emailLogs(context)
                FRAME_FREE_ACTIVE, FRAME_UNDO, FRAME_REMOVE_RED, FRAME_LAST_BLACK_FOULED, FRAME_RESPOT_BLACK -> gameVm.assignPot(action.getPotType())
                FRAME_MISS_FORFEIT -> gameVm.onEventGameAction(
                    action.queryEndFrameOrMatch(
                        gameVm.score.isMatchEnding(),
                        gameVm.isFrameMathematicallyOver()
                    )
                )
                FRAME_TO_END, FRAME_ENDED, MATCH_TO_END, MATCH_ENDED -> gameVm.endFrame(action)
                FRAME_RERACK, FRAME_START_NEW -> {
                    showInterstitialAd(context)
                    gameVm.resetFrame(action)
                }
                MATCH_ENDED_DISCARD_FRAME -> {
                    mainVm.deleteCrtFrameFromDb()
                    gameVm.onEventGameAction(NAV_TO_POST_MATCH)
                }
                NAV_TO_POST_MATCH -> mainVm.onEmit(ScreenEvents.Navigate(Screen.Summary.route))
                MATCH_CANCEL -> {
                    mainVm.deleteMatchFromDb()
                    mainVm.onEmit(ScreenEvents.Navigate(Screen.Rules.route))
                }
                else -> Timber.i("No implementation for observed action $action")
            }
        }
    }

    var crtPlayer by remember { mutableStateOf(crtPlayer) }
    LaunchedEffect(domainFrame) {
        crtPlayer = Settings.crtPlayer
    }

    FragmentContent(paddingValues = PaddingValues(MaterialTheme.spacing.default), withBottomSpacer = false) {
        DialogGeneric(dialogVm, gameVm)
        DialogFoul(gameVm, dialogVm, mainVm)

        Column(
            Modifier
                .background(Transparent)
                .padding(8.dp, 0.dp)
        ) {
            GameModuleContainer { GameModulePlayerNames(crtPlayer) }
            Spacer(modifier = Modifier.height(8.dp))
            GameModuleContainer { GameModuleScore(domainFrame) }
            GameModuleContainer { GameModuleStatistics(domainFrame.score) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        GameModuleContainer(
            modifier = Modifier.weight(1f),
            spacerSize = 60.dp
        ) { GameModuleBreaks(domainFrame.frameStack) }
        GameModuleActions(gameVm, domainFrame.ballStack)
    }

    BackPressHandler { gameVm.onEventGameAction(if (gameVm.score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL) }
}

@Composable
fun GameModuleContainer(
    modifier: Modifier = Modifier,
    title: String = Constants.EMPTY_STRING,
    spacerSize: Dp = MaterialTheme.spacing.default,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier) {
    if (title != Constants.EMPTY_STRING) ModuleTitle(title)
    content()
    Spacer(Modifier.size(spacerSize))
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



