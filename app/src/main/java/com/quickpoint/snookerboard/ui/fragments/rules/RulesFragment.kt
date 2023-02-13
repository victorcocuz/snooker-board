package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogGeneric
import com.quickpoint.snookerboard.ui.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.GenericViewModelFactory

@Composable
fun ScreenRules(
    navController: NavController,
    mainVm: MainViewModel,
    dataStore: DataStore,
) {
    val rulesVm: RulesViewModel = viewModel(factory = GenericViewModelFactory(dataStore))
    val dialogVm: DialogViewModel = viewModel(factory = GenericViewModelFactory())
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        mainVm.turnOffSplashScreen()
        rulesVm.eventSharedFlow.collect { mainVm.onEmit(it) }
    }

    FragmentContent(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }) {
        ColumnBasicRules(rulesVm)
        ColumnAdvancedRules(rulesVm, dialogVm, Toggle.AdvancedRules.isEnabled)
        DialogGeneric(dialogVm)
        Button(
            shape = RoundedCornerShape(50.dp),
            onClick = {
                rulesVm.startMatchQuery()
            },
        ) {
            Text(text = "Start Match")
        }
    }
}