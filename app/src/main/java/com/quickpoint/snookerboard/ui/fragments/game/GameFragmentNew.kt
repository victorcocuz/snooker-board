package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.isMatchEnding
import com.quickpoint.snookerboard.domain.isNoFrameFinished
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.RowHorizontalDivider
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogFoul
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*
import timber.log.Timber

@Composable
fun ScreenGame(
    navController: NavController,
    mainVm: MainViewModel,
    gameVm: GameViewModel,
    dataStore: DataStore,
) {
    val dialogVm: DialogViewModel = viewModel(factory = GenericViewModelFactory())

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        gameVm.eventAction.collect { action ->
            Timber.e("action is $action")
            when (action) {
                // Directly observed from gameVm
                TRANSITION_TO_FRAGMENT -> mainVm.turnOffSplashScreen(200)
                FRAME_UPDATED -> {
//                    requireActivity().invalidateOptionsMenu() // Reset the menu every time the frame has been updated
//                    Timber.i(getString(R.string.helper_update_frame_info))
                }
                SNACK_UNDO, SNACK_ADD_RED, SNACK_REMOVE_COLOR, SNACK_FRAME_RERACK_DIALOG,
                SNACK_FRAME_ENDING_DIALOG, SNACK_MATCH_ENDING_DIALOG, SNACK_NO_BALL,
                -> {
                } // snackbar action

                // Dialogs relating
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
//                    adMob.showInterstitialAd()
                    gameVm.resetFrame(action)
                }
                MATCH_ENDED_DISCARD_FRAME -> {
                    gameVm.deleteCrtFrameFromDb()
                    gameVm.onEventGameAction(NAV_TO_POST_MATCH)
                }
                NAV_TO_POST_MATCH -> {
                    Settings.matchState = MatchState.SUMMARY
//                        navigate(GameFragmentDirections.summaryFrag(), adMob)
                }
                MATCH_CANCEL -> {
                    gameVm.deleteMatchFromDb()
//                        navigate(GameFragmentDirections.rulesFrag(), adMob)
                }
                else -> Timber.i("No implementation for observed action $action")
            }
        }
    }

    val domainFrame by gameVm.frameState.collectAsState()

    LaunchedEffect(true) {
        if (Settings.matchState == MatchState.RULES_IDLE) {
            gameVm.resetMatch()
            Settings.matchState = MatchState.GAME_IN_PROGRESS
        } else mainVm.eventStoredFrame.collect { event ->
            gameVm.loadMatch(event.getContentIfNotHandled())
        }
    }

    var crtPlayer by remember { mutableStateOf(Settings.crtPlayer) }
    LaunchedEffect(domainFrame) {
        crtPlayer = Settings.crtPlayer
    }

    FragmentContent(paddingValues = PaddingValues(MaterialTheme.spacing.default), withBottomSpacer = false) {
        DialogGeneric(dialogVm, gameVm)
        DialogFoul(gameVm, dialogVm, mainVm)
        GameModulePlayerNames(crtPlayer)
        GameModuleContainer(
            title = stringResource(R.string.l_game_score_ll_line_module_score)
        ) { GameModuleScore(domainFrame) }
//        GameModuleContainer(
//            title = stringResource(R.string.l_game_score_ll_line_module_statistics)
//        ) { GameModuleStatistics(domainFrame.score) }
        Box(Modifier.weight(1f)) {
            GameModuleContainer(
                modifier = Modifier.fillMaxSize(),
                title = stringResource(R.string.l_game_score_ll_line_module_breaks),
                spacerSize = 60.dp
            ) { GameModuleBreaks(domainFrame.frameStack) }
//            Row(Modifier.align(Alignment.BottomEnd)) { ActionButtonsToggles(gameVm, isLongSelected, isRestSelected) }
        }
        GameModuleActions(gameVm, domainFrame.ballStack)
    }
}

@Composable // Game
fun GameModuleContainer(
    modifier: Modifier = Modifier,
    title: String = Constants.EMPTY_STRING,
    spacerSize: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier = modifier.padding(MaterialTheme.spacing.medium, 0.dp)) {
    if (title != "") ModuleTitle(title)
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



