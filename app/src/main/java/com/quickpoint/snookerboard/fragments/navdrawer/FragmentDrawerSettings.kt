package com.quickpoint.snookerboard.fragments.navdrawer

import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraph
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.domain.objects.getToggleByKey
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_BREAKS
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_RULES
import com.quickpoint.snookerboard.utils.K_BOOL_TOGGLE_ADVANCED_STATISTICS

@Composable
fun FragmentDrawerSettings(
    mainVm: MainViewModel
) {
    LaunchedEffect(key1 = true) {

    }
    FragmentColumn {
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_RULES, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_STATISTICS, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(K_BOOL_TOGGLE_ADVANCED_BREAKS, mainVm)
    }
}

@Composable
fun SettingsToggleHoist(key: String, mainVm: MainViewModel) {
    val togglesEvent: Event<Unit> by mainVm.eventToggleChange.observeAsState(Event(Unit))
    togglesEvent.getContentIfNotHandled() // Simply used to call the observer, only to trigger composition
    SettingsToggle(
        stringResource(
            when (key) {
                K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_title
                K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_title
                K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_title
                else -> R.string.helper_not_implemented
            }
        ),
        stringResource(
            when (key) {
                K_BOOL_TOGGLE_ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_description
                K_BOOL_TOGGLE_ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_description
                K_BOOL_TOGGLE_ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_description
                else -> R.string.helper_not_implemented
            }
        ),
        getToggleByKey(key)?.isEnabled ?: true
    ) { mainVm.onToggleChange(key) }
}

@Composable
fun SettingsToggle(title: String, description: String, isChecked: Boolean, onClickChange: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(1f)
            .clickable(onClick = onClickChange)
            .padding(MaterialTheme.spacing.small, 0.dp, MaterialTheme.spacing.small, MaterialTheme.spacing.small)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            TextNavParagraphSubTitle(title)
            TextNavParagraph(description)
        }

        AndroidView(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, 0.dp, 0.dp)
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

//@Preview(showBackground = true)
//@Composable
//fun FragmentDrawerSettingsPreview() {
//    FragmentDrawerSettings(rememberNavController())
//}