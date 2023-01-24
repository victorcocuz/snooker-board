package com.quickpoint.snookerboard.fragments.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.MainViewModelOld
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ScreenEvents
import com.quickpoint.snookerboard.compose.ui.styles.*
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.*
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.fragments.gamedialogs.DialogViewModel
import com.quickpoint.snookerboard.fragments.gamedialogs.FragmentDialogGeneric
import com.quickpoint.snookerboard.utils.*
import timber.log.Timber

class RulesFragmentOld : Fragment() {
    // Variables
    private val mainVm: MainViewModelOld by activityViewModels()
    private val rulesVm: RulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideKeyboard()

        // Check match state and navigate to the correct fragment when applicable
        postponeEnterTransition() // Wait for data to load before displaying fragment
        if (Settings.matchState == RULES_IDLE) mainVm.transitionToFragment(this, 0) // For returning from navDrawer
        mainVm.matchState.observe(viewLifecycleOwner, EventObserver { matchState ->
            Timber.i("ObservedOnce matchState: $matchState")
            when (matchState) {
                RULES_IDLE -> {
                    mainVm.transitionToFragment(this, 0)
                }

                GAME_IN_PROGRESS, SUMMARY -> {
                    startPostponedEnterTransition()
                    view?.visibility = GONE
                    navigate(if (matchState == GAME_IN_PROGRESS) RulesFragmentDirections.gameFrag() else RulesFragmentDirections.summaryFrag())
                }

                else -> Timber.e("No implementation for state $matchState at this point")
            }
        })

        return ComposeView(requireContext()).apply {
            setContent {}
        }
    }
}
