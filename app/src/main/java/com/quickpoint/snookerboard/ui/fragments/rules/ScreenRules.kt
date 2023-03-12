package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.ui.components.BackPressHandler
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.FragmentExtras
import com.quickpoint.snookerboard.ui.components.MainButton
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel

@Composable
fun ScreenRules() {
    val mainVm = LocalView.current.findViewTreeViewModelStoreOwner().let { hiltViewModel<MainViewModel>(it!!) }
    val rulesVm = hiltViewModel<RulesViewModel>()
    val dialogVm = hiltViewModel<DialogViewModel>()
    val focusManager = LocalFocusManager.current

    mainVm.setupActionBarActions(emptyList(), emptyList()) { }

    val isAdvancedRules by rulesVm.dataStoreRepository.toggleAdvancedRules.collectAsState(false)

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