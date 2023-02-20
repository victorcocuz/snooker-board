package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.components.ButtonStandardHoist
import com.quickpoint.snookerboard.ui.components.RulesHandicapLabel
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.utils.K_INT_MATCH_AVAILABLE_REDS
import com.quickpoint.snookerboard.utils.K_INT_MATCH_FOUL_MODIFIER
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_MATCH

@Composable
fun ColumnAdvancedRules(rulesVm: RulesViewModel, dialogVm: DialogViewModel, show: Boolean) {
    if (show) Column {
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_reds_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 6)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 10)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_AVAILABLE_REDS, value = 15)
            })
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_foul_modifier_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -3)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -2)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = -1)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_FOUL_MODIFIER, value = 0)
            },
            contentIcon = {
                Icon(
                    modifier = Modifier.clickable { dialogVm.onOpenGenericDialog() },
                    painter = painterResource(id = R.drawable.ic_temp_info),
                    contentDescription = null
                )
            }
        )
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_handicap_frame_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = -10)
                RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_FRAME, value = 10)
            })
        RuleSelectionItem(
            title = stringResource(R.string.l_rules_extra_tv_handicap_match_label),
            content = {
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = -1)
                RulesHandicapLabel(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH)
                ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_HANDICAP_MATCH, value = 1)
            })
    }
}