package com.quickpoint.snookerboard.ui.fragments.rules

import android.graphics.Color
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.objects.MatchSettings
import com.quickpoint.snookerboard.ui.components.ButtonStandardHoist
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.K_INT_MATCH_AVAILABLE_FRAMES
import com.quickpoint.snookerboard.utils.K_INT_MATCH_STARTING_PLAYER

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColumnBasicRules(rulesVm: RulesViewModel) = Column {
    RulesModuleNames(rulesVm, LocalFocusManager.current, LocalSoftwareKeyboardController.current)
    RuleSelectionItem(
        title = stringResource(R.string.l_rules_main_tv_frames_label),
        content = { NumberPickerHoist(rulesVm = rulesVm) }
    )
    RuleSelectionItem(
        title = stringResource(R.string.l_rules_main_tv_breaks_first_label),
        content = {
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 0)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 2)
            ButtonStandardHoist(rulesVm = rulesVm, key = K_INT_MATCH_STARTING_PLAYER, value = 1)
        })
}

@Composable
fun NumberPickerHoist(
    modifier: Modifier = Modifier,
    rulesVm: RulesViewModel,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    rulesUpdateAction.peekContent() // Used to refresh composition when rules change
    NumberPicker(modifier, rulesVm, MatchSettings.Settings.availableFrames)
}

@Composable
fun NumberPicker(
    modifier: Modifier,
    rulesVm: RulesViewModel,
    newValue: Int
) {
    AndroidView( // Number Picker
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, 0.dp, 0.dp),
        factory = { context ->
            com.shawnlin.numberpicker.NumberPicker(context).apply {
                dividerColor = Color.WHITE
                setDividerDistance(360)
                orientation = LinearLayout.HORIZONTAL
                selectedTextColor = Color.WHITE
                textColor = Color.WHITE
                minValue = 1
                maxValue = 19
                value = newValue
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                setOnValueChangedListener { _, _, newVal ->
                    rulesVm.onMatchSettingsChange(K_INT_MATCH_AVAILABLE_FRAMES, newVal)
                }
            }
        },
        update = {
            it.value = newValue
        })
}