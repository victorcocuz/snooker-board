package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.isFrameEqual
import com.quickpoint.snookerboard.ui.components.ButtonStandard
import com.quickpoint.snookerboard.ui.components.GenericDialog
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.ui.fragments.rules.RuleSelectionItem
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*

@Composable
fun DialogGeneric(dialogVm: DialogViewModel, gameVm: GameViewModel? = null) {
    val matchActions by dialogVm.matchActions.collectAsState()

    LaunchedEffect(key1 = true) {
        dialogVm.eventDialogAction.collect { matchAction ->
            gameVm?.onEventGameAction(
                matchAction, when (matchAction) {
                    MATCH_CANCEL, FRAME_RERACK, FRAME_START_NEW -> true
                    else -> false
                }
            )
        }
    }
    if (dialogVm.isGenericDialogShown) {
        FragmentDialogGeneric(
            gameVm = gameVm,
            matchActions = matchActions,
            onDismiss = { dialogVm.onDismissGenericDialog() },
            onConfirm = { matchAction -> dialogVm.onEventDialogAction(matchAction) }
        )
    }
}

@Composable
fun FragmentDialogGeneric(
    gameVm: GameViewModel?,
    matchActions: List<MatchAction>,
    onDismiss: () -> Unit,
    onConfirm: (MatchAction) -> Unit,
) {
    val domainFrame = gameVm?.frameState?.collectAsState()
    val isCancelable = matchActions[2] !in listOfMatchActionsUncancelable

    GenericDialog(
        onDismissRequest = { onDismiss() },
        isCancelable = isCancelable
    ) {
        TextSubtitle(getGenericDialogTitleText(matchActions[1], matchActions[2]))
        TextSubtitle(getGenericDialogQuestionText(matchActions[1], matchActions[2]))
        if (matchActions[1] in listOf(MATCH_ENDED_DISCARD_FRAME, FRAME_MISS_FORFEIT))
            TextParagraph(getDialogGameNote(matchActions[1], domainFrame?.value?.score))
        Divider()
        RuleSelectionItem(
            title = stringResource(R.string.d_generic_module_actions),
            content = {
                if (matchActions[0] != IGNORE) ButtonStandard(text = stringResource(R.string.d_generic_answer_a_generic)) {
                    onConfirm(
                        matchActions[0]
                    )
                }
                if (matchActions[1] == MATCH_ENDED_DISCARD_FRAME) ButtonStandard(text = getDialogGameBText(matchActions[1])) {
                    onConfirm(
                        matchActions[1]
                    )
                }
                if (!(matchActions[1] !in listOf(MATCH_ENDED_DISCARD_FRAME, IGNORE) && domainFrame?.value?.score?.isFrameEqual() == true))
                    ButtonStandard(text = getDialogGameCText(matchActions[1], matchActions[2])) { onConfirm(matchActions[2]) }
            })
    }
}