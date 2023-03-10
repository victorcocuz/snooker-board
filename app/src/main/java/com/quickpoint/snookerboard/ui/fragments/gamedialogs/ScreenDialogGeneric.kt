package com.quickpoint.snookerboard.ui.fragments.gamedialogs

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.isFrameEqual
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*

@Composable
fun DialogGeneric(
    dialogVm: DialogViewModel, gameVm: GameViewModel? = null, onDismiss: () -> Unit,
    onConfirm: (MatchAction) -> Unit,
) {
    val matchActions by dialogVm.matchActions.collectAsState()

    LaunchedEffect(Unit) {
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
        val domainFrame = gameVm?.frameState?.collectAsState()
        val isCancelable = matchActions[2] !in listOfMatchActionsUncancelable

        GenericDialog(onDismissRequest = { onDismiss() }, isCancelable = isCancelable) {
            TextSubtitle(getGenericDialogTitleText(matchActions[1], matchActions[2]))
            TextSubtitle(getGenericDialogQuestionText(matchActions[1], matchActions[2]))
            if (matchActions[1] in listOf(MATCH_ENDED_DISCARD_FRAME, FRAME_MISS_FORFEIT))
                TextParagraph(getDialogGameNote(matchActions[1], domainFrame?.value?.score))
            Divider()
            ContainerRow(title = stringResource(R.string.d_generic_module_actions)) {
                if (matchActions[0] != IGNORE)
                    ButtonStandard(text = stringResource(R.string.d_generic_answer_a_generic)) { onConfirm(matchActions[0]) }
                if (matchActions[1] == MATCH_ENDED_DISCARD_FRAME)
                    ButtonStandard(text = getDialogGameBText(matchActions[1])) { onConfirm(matchActions[1]) }
                if (!(matchActions[1] !in listOf(MATCH_ENDED_DISCARD_FRAME, IGNORE) && domainFrame?.value?.score?.isFrameEqual() == true))
                    ButtonStandard(text = getDialogGameCText(matchActions[1], matchActions[2])) { onConfirm(matchActions[2]) }
            }
        }
    }
}
