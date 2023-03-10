package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.ui.components.BackPressHandler
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.FragmentExtras
import com.quickpoint.snookerboard.ui.components.MainButton
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.GenericViewModelFactory

@Composable
fun ScreenRules(
    mainVm: MainViewModel,
    dataStore: DataStore
) {
    val rulesVm: RulesViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val dialogVm: DialogViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val focusManager = LocalFocusManager.current

    mainVm.setupActionBarActions(emptyList(), emptyList()) { }

    val isAdvancedRules by mainVm.toggleAdvancedRules.collectAsState(false)

    LaunchedEffect(Unit) {
        mainVm.turnOffSplashScreen()
        rulesVm.eventSharedFlow.collect { mainVm.onEmit(it) }
    }

    FragmentContent(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }) {
        ModuleRulesBasic(rulesVm)
        ModuleRulesAdvanced(rulesVm, dialogVm, isAdvancedRules)
        MainButton("Start Match") { rulesVm.startMatchQuery() }
    }

    FragmentExtras {
        DialogGeneric(dialogVm,
            onDismiss = { dialogVm.onDismissGenericDialog() },
            onConfirm = { matchAction -> dialogVm.onEventDialogAction(matchAction) })
        BackPressHandler { }
    }
}