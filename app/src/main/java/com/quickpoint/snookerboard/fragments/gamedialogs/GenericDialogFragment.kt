package com.quickpoint.snookerboard.fragments.gamedialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.ButtonStandard
import com.quickpoint.snookerboard.compose.ui.styles.RuleSelectionItem
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraph
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraphSubTitle
import com.quickpoint.snookerboard.databinding.FragmentDialogGenBinding
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.fragments.game.GameFragment
import com.quickpoint.snookerboard.fragments.game.GameViewModel
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.*


class GenericDialogFragment : DialogFragment() {
    private val dialogVm: DialogViewModel by activityViewModels()
    private var gameVm: GameViewModel? = null
    private lateinit var matchAction: MatchAction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentDialogGenBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_gen, container, false)

        if (requireParentFragment().childFragmentManager.fragments[0] is GameFragment)
            gameVm = ViewModelProvider(requireParentFragment().childFragmentManager.fragments[0])[GameViewModel::class.java]

        // Observers
        dialogVm.eventDialogAction.observe(viewLifecycleOwner, EventObserver { action ->
            matchAction = action
            dismiss() // Close dialog once a match action as been clicked on
        })

        return binding.root
    }

    override fun onDestroy() { // Pass action back here to avoid crash during navigation
        super.onDestroy()
        if (this::matchAction.isInitialized) gameVm?.onEventGameAction(
            matchAction, when (matchAction) {
                MATCH_CANCEL, FRAME_RERACK, FRAME_START_NEW -> true
                else -> false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FragmentDialogGeneric(
    gameVm: GameViewModel? = null,
    matchActions: List<MatchAction>,
    onDismiss: () -> Unit,
    onConfirm: (MatchAction) -> Unit,
) {
    val score = gameVm?.displayFrame?.value?.score
    val isCancelable = matchActions[2] !in listOfMatchActionsUncancelable
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = isCancelable,
            dismissOnClickOutside = isCancelable
        )
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .border(1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                TextParagraphSubTitle(getGenericDialogTitleText(matchActions[1], matchActions[2]))
                TextParagraphSubTitle(getGenericDialogQuestionText(matchActions[1], matchActions[2]))
                if (matchActions[1] in listOf(MATCH_ENDED_DISCARD_FRAME, FRAME_MISS_FORFEIT))
                    TextParagraph(getDialogGameNote(matchActions[1], score))
                Divider()
                RuleSelectionItem(
                    title = "Actions",
                    content = {
                        if (matchActions[0] != IGNORE)
                            ButtonGenericDialogHoist(onAction = { onConfirm(matchActions[0]) }, text = "No")
                        if (matchActions[1] == MATCH_ENDED_DISCARD_FRAME)
                            ButtonGenericDialogHoist(onAction = { onConfirm(matchActions[1]) }, text = getDialogGameBText(matchActions[1]))
                        if (!(matchActions[1] !in listOf(MATCH_ENDED_DISCARD_FRAME, IGNORE) && (score?.isFrameEqual() == true)))
                            ButtonGenericDialogHoist(onAction = { onConfirm(matchActions[2]) }, text = getDialogGameCText(matchActions[1], matchActions[2]))
                    })
            }
        }
    }
}

@Composable
fun ButtonGenericDialogHoist(text: String, onAction: () -> Unit, ) {
    ButtonStandard(text = text, onClick = { onAction() })
}

fun getGenericDialogTitleText(matchActionB: MatchAction, matchActionC: MatchAction): String = when {
    matchActionB == FRAME_MISS_FORFEIT -> "Forfeit frame"
    matchActionC == MATCH_CANCEL -> "Cancel match"
    matchActionC == FRAME_RERACK -> "Rerack"
    matchActionC == FRAME_TO_END -> "Concede frame"
    matchActionC == MATCH_TO_END -> "Concede match"
    matchActionC == FRAME_ENDED -> "Frame ended"
    matchActionC == MATCH_ENDED -> "Match ended"
    matchActionC == FOUL_DIALOG -> "Foul"
    matchActionC == INFO_FOUL_DIALOG -> "Info foul"
    matchActionC == FRAME_LAST_BLACK_FOULED -> "Last black fouled"
    matchActionC == FRAME_RESPOT_BLACK -> "Re-spot black"
    matchActionC == FRAME_LOG_ACTIONS -> "Actions log"
    else -> "$matchActionC not implemented"
}

fun getGenericDialogQuestionText(matchActionB: MatchAction, matchActionC: MatchAction): String = when {
    matchActionB == FRAME_MISS_FORFEIT -> "If a player commits Foul and a Miss three times in a row while the object ball is fully visible, this results in the frame being automatically lost. If this is the case, please choose to forfeit frame."
    matchActionC == MATCH_CANCEL -> "Are you sure you want to cancel the current match? All match progress will be lost."
    matchActionC == FRAME_RERACK -> "Are you sure you want to rerack? All frame progress will be lost."
    matchActionC == FRAME_TO_END -> "This frame is still in progress, are you sure you want to end it?"
    matchActionC == MATCH_TO_END -> "This match is still in progress, are you sure you want to end it?"
    matchActionC == FRAME_ENDED -> "This frame has mathematically ended. Would you like to proceed?"
    matchActionC == MATCH_ENDED -> "This match has mathematically ended. Would you like to proceed?"
    matchActionC == INFO_FOUL_DIALOG -> "A typical foul in snooker is worth 4 points. You may wish to decrease this value."
    matchActionC == FRAME_LAST_BLACK_FOULED -> "There was a foul committed on the last black. According to regulations, the frame is now over."
    matchActionC == FRAME_RESPOT_BLACK -> "Looks like you and your opponent are tied at the end of the frame! The black ball will be re-spotted to decide the winner. The player who started the frame will attempt to pot the black first."
    matchActionC == FRAME_LOG_ACTIONS -> "If you've experienced any issues during this match you can submit an action log, which will be reviewed by the developer. Would you like to submit an action log?"
    else -> "$matchActionC not implemented"
}

fun getDialogGameNote(matchAction: MatchAction, score: MutableList<DomainScore>?): String = when {
    matchAction == FRAME_MISS_FORFEIT -> "NOTE: Please read carefully, this action cannot be undone"
    score == null -> ""
    score.isNoFrameFinished() -> ""
    score.isFrameWinResultingMatchTie() -> "NOTE: Keeping the current frame will result in a draw"
    score.isMatchEqual() -> "NOTE: Discarding the current frame will result in a draw"
    else -> ""
}

fun getDialogGameCText(matchActionB: MatchAction, matchActionC: MatchAction): String = when {
    matchActionC in listOf(INFO_FOUL_DIALOG, FRAME_LAST_BLACK_FOULED, FRAME_RESPOT_BLACK) -> "I understand"
    matchActionB == FRAME_MISS_FORFEIT -> "Yes, forfeit the frame"
    else -> "Yes"
}

fun getDialogGameBText(matchActionB: MatchAction): String = when (matchActionB) {
    MATCH_ENDED_DISCARD_FRAME -> "Yes & remove this frame"
    else -> ""
}

//@Preview(showBackground = true)
//@Composable
//fun FragmentDialogGenericPreview() {
//    FragmentDialogGeneric(rememberNavController())
//}
