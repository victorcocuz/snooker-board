package com.quickpoint.snookerboard.ui.fragments.navdrawer

import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.objects.getToggleByKey
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.*

@Composable
fun ScreenDrawerSettings(dataStore: DataStore) {
    val drawersVm: DrawersViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val togglesEvent: Event<Unit> by drawersVm.eventToggleChange.observeAsState(Event(Unit))
    togglesEvent.peekContent() // Simply used to trigger composition
    val context = LocalContext.current

    FragmentContent {
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_RULES) { drawersVm.onToggleChange(it, context)}
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_STATISTICS) { drawersVm.onToggleChange(it, context)}
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_BREAKS) { drawersVm.onToggleChange(it, context)}
    }
}

@Composable
fun SettingsToggleHoist(key: String, onClick: (String) -> Unit) {
    SettingsToggle(
        stringResource(key.getSettingsTitleId()),
        stringResource(key.getSettingsTextId()),
        getToggleByKey(key)?.isEnabled ?: true
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
            TextSubtitle(title)
            TextParagraph(description)
        }
        AndroidView(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, MaterialTheme.spacing.default, MaterialTheme.spacing.default)
                .align(Alignment.CenterVertically),
            factory = { context ->
                SwitchCompat(context).apply {
                    isClickable = false
                    setTrackResource(R.drawable.toggle_slider_track)
                    setThumbResource(R.drawable.toggle_slider_thumb)
                    setChecked(isChecked)
                }
            },
            update = {
                it.isChecked = isChecked
            })
    }
}

fun String.getSettingsTextId() = when (this) {
    K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_description
    K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_description
    K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_description
    else -> R.string.helper_not_implemented
}

fun String.getSettingsTitleId() = when (this) {
    K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_title
    K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_title
    K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_title
    else -> R.string.helper_not_implemented
}

//@Preview(showBackground = true)
//@Composable
//fun FragmentDrawerSettingsPreview() {
//    FragmentDrawerSettings(rememberNavController())
//}