package com.quickpoint.snookerboard.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.core.admob.showInterstitialAd
import com.quickpoint.snookerboard.core.utils.MatchAction.*
import com.quickpoint.snookerboard.core.utils.getListOfDialogActions
import com.quickpoint.snookerboard.core.utils.getPotType
import com.quickpoint.snookerboard.core.utils.queryEndFrameOrMatch
import com.quickpoint.snookerboard.domain.models.PotType
import com.quickpoint.snookerboard.domain.models.isMatchEnding
import com.quickpoint.snookerboard.domain.models.isMatchInProgress
import com.quickpoint.snookerboard.domain.models.isNoFrameFinished
import com.quickpoint.snookerboard.domain.utils.MatchState
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.domain.utils.MatchSettings.Companion.crtPlayer
import com.quickpoint.snookerboard.ui.components.BackPressHandler
import com.quickpoint.snookerboard.ui.components.ComponentPlayerNames
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.FragmentExtras
import com.quickpoint.snookerboard.ui.screens.gamedialogs.DialogFoul
import com.quickpoint.snookerboard.ui.screens.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.screens.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.ui.navigation.Screen
import com.quickpoint.snookerboard.ui.navigation.getActionItems
import com.quickpoint.snookerboard.ui.navigation.getActionItemsOverflow
import com.quickpoint.snookerboard.ui.theme.Transparent
import com.quickpoint.snookerboard.ui.theme.spacing
import timber.log.Timber

@Composable
fun ScreenGame() {
    val mainVm = LocalView.current.findViewTreeViewModelStoreOwner().let { hiltViewModel<MainViewModel>(it!!) }
    val gameVm = hiltViewModel<GameViewModel>()
    val dialogVm = hiltViewModel<DialogViewModel>()
    val domainFrame by gameVm.frameState.collectAsState()
    val context = LocalContext.current
    val isFreeballActive by gameVm.dataStoreRepository.toggleFreeball.collectAsState(false)
    val isAdvancedBreaksActive by gameVm.dataStoreRepository.toggleAdvancedBreaks.collectAsState(false)

    mainVm.setupActionBarActions(
        gameVm.getActionItems(),
        gameVm.getActionItemsOverflow()
    ) { gameVm.onMenuItemSelected(it) }

    LaunchedEffect(Unit) {
        if (MatchSettings.matchState == MatchState.RULES_IDLE) {
            gameVm.resetMatch()
            MatchSettings.matchState = MatchState.GAME_IN_PROGRESS
        } else gameVm.loadMatch(mainVm.cachedFrame)

        gameVm.eventAction.collect { action ->
            when (action) {
                // Directly observed from gameVm
                FRAME_UPDATED -> Timber.i(context.getString(R.string.helper_update_frame_info))
                SNACK_UNDO, SNACK_ADD_RED, SNACK_REMOVE_COLOR, SNACK_FRAME_RERACK_DIALOG,
                SNACK_FRAME_ENDING_DIALOG, SNACK_MATCH_ENDING_DIALOG, SNACK_NO_BALL,
                -> mainVm.onEmit(ScreenEvents.SnackAction(action))

                // Concerning dialogs
                FOUL_DIALOG -> dialogVm.onOpenFoulDialog()
                FOUL_CONFIRM -> {
                    gameVm.assignPot(PotType.TYPE_FOUL, dialogVm.ballClicked!!, dialogVm.actionClicked.value) // Temp Pot Action
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
        crtPlayer = MatchSettings.crtPlayer
    }

    FragmentContent(
        Modifier.background(Transparent),
        paddingValues = PaddingValues(MaterialTheme.spacing.default),
        showBottomSpacer = false
    ) {
        ComponentPlayerNames(crtPlayer)
        ModuleGameScore(domainFrame)
        ModuleGameStatistics(domainFrame.score, domainFrame.score.size == 2)
        ModuleGameBreaks(domainFrame.frameStack, isAdvancedBreaksActive)
        ModuleGameActions(gameVm, domainFrame.ballStack, domainFrame.frameStack)
    }

    FragmentExtras {
        DialogGeneric(dialogVm, gameVm,
            onDismiss = { dialogVm.onDismissGenericDialog() },
            onConfirm = { matchAction -> dialogVm.onEventDialogAction(matchAction) })
        DialogFoul(gameVm, dialogVm,
            onDismiss = { dialogVm.onDismissFoulDialog() },
            onConfirm = {
                if (dialogVm.foulIsValid()) {
                    repeat(dialogVm.eventDialogReds.value) { gameVm.onEventGameAction(FRAME_REMOVE_RED, true) }
                    gameVm.onEventGameAction(FOUL_CONFIRM, true)
                    if (isFreeballActive) gameVm.onEventGameAction(FRAME_FREE_ACTIVE, true)
                } else mainVm.onEmit(ScreenEvents.SnackAction(SNACK_INVALID_FOUL))
            })
        BackPressHandler { gameVm.onEventGameAction(if (gameVm.score.isMatchInProgress()) MATCH_CANCEL_DIALOG else MATCH_CANCEL) }
    }
}




