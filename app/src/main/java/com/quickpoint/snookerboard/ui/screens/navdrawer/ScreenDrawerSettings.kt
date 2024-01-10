package com.quickpoint.snookerboard.ui.screens.navdrawer

import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_ADVANCED_BREAKS
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_ADVANCED_RULES
import com.quickpoint.snookerboard.data.K_BOOL_TOGGLE_ADVANCED_STATISTICS
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.SingleParagraph
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun ScreenDrawerSettings() {
    val drawersVm = hiltViewModel<DrawersViewModel>()
    val context = LocalContext.current

    val isAdvancedRules by drawersVm.dataStoreRepository.toggleAdvancedRules.collectAsState(false)
    val isAdvancedStatistics by drawersVm.dataStoreRepository.toggleAdvancedStatistics.collectAsState(false)
    val isAdvancedBreaks by drawersVm.dataStoreRepository.toggleAdvancedBreaks.collectAsState(false)

    FragmentContent {
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_RULES, isAdvancedRules) { drawersVm.onToggleChange(it, context)}
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_STATISTICS, isAdvancedStatistics) { drawersVm.onToggleChange(it, context)}
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_BREAKS, isAdvancedBreaks) { drawersVm.onToggleChange(it, context)}
    }
}

@Composable
fun SettingsToggleHoist(key: String, value: Boolean, onClick: (String) -> Unit) {
    SettingsToggle(
        stringResource(key.getSettingsTitleId()),
        stringResource(key.getSettingsTextId()),
        value
    ) { onClick(key) }
}

@Composable
fun SettingsToggle(title: String, description: String, isChecked: Boolean, onClickChange: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(1f)
            .padding(MaterialTheme.spacing.small, MaterialTheme.spacing.large, MaterialTheme.spacing.small, MaterialTheme.spacing.small)
            .clickable(onClick = onClickChange)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            SingleParagraph(title, description)
        }
        AndroidView(
            modifier = Modifier
                .padding(
                    MaterialTheme.spacing.medium,
                    MaterialTheme.spacing.small,
                    MaterialTheme.spacing.default,
                    MaterialTheme.spacing.default
                )
                .align(Alignment.CenterVertically),
            factory = { context ->
                SwitchCompat(context).apply {
                    isClickable = false
                    setTrackResource(R.drawable.toggle_slider_track)
                    setThumbResource(R.drawable.toggle_slider_thumb)
                    setChecked(isChecked)
                }
            },
            update = { switch ->
                switch.isChecked = isChecked
            })
    }
}

fun String.getSettingsTextId() = when (this) {
    K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.dr_settings_toggle_advanced_rules_description
    K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.dr_settings_toggle_advanced_statistics_description
    K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.dr_settings_toggle_advanced_breaks_description
    else -> R.string.helper_not_implemented
}

fun String.getSettingsTitleId() = when (this) {
    K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.dr_settings_toggle_advanced_rules_title
    K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.dr_settings_toggle_advanced_statistics_title
    K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.dr_settings_toggle_advanced_breaks_title
    else -> R.string.helper_not_implemented
}