package com.quickpoint.snookerboard.ui.screens.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.MatchAction.INFO_FOUL_DIALOG
import com.quickpoint.snookerboard.core.utils.getListOfDialogActions
import com.quickpoint.snookerboard.data.K_INT_MATCH_AVAILABLE_REDS
import com.quickpoint.snookerboard.data.K_INT_MATCH_FOUL_MODIFIER
import com.quickpoint.snookerboard.data.K_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.data.K_INT_MATCH_HANDICAP_MATCH
import com.quickpoint.snookerboard.ui.components.ButtonStandardHoist
import com.quickpoint.snookerboard.ui.components.ContainerRow
import com.quickpoint.snookerboard.ui.components.IconInfo
import com.quickpoint.snookerboard.ui.components.RulesHandicapLabel
import com.quickpoint.snookerboard.ui.screens.gamedialogs.DialogViewModel

@Composable
fun ModuleRulesAdvanced(rulesVm: RulesViewModel, dialogVm: DialogViewModel, show: Boolean) {
    if (show) Column {
        ContainerRow(title = stringResource(R.string.l_rules_extra_tv_reds_label)) {
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 6)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 10)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 15)
        }
        ContainerRow(
            title = stringResource(R.string.l_rules_extra_tv_foul_modifier_label),
            trailingIcon = { IconInfo(Modifier.clickable { dialogVm.onOpenGenericDialog(INFO_FOUL_DIALOG.getListOfDialogActions()) }) }
        ) {
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -3)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -2)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -1)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = 0)
        }
        ContainerRow(title = stringResource(R.string.l_rules_extra_tv_handicap_frame_label)) {
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = -10)
            RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = 10)
        }
        ContainerRow(title = stringResource(R.string.l_rules_extra_tv_handicap_match_label)) {
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = -1)
            RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = 1)
        }
    }
}