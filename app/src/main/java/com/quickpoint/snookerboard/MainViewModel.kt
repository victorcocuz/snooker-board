package com.quickpoint.snookerboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.data.DataStore
import com.quickpoint.snookerboard.data.database.models.asDomain
import com.quickpoint.snookerboard.domain.models.DomainFrame
import com.quickpoint.snookerboard.domain.repository.GameRepository
import com.quickpoint.snookerboard.domain.utils.DomainPlayer
import com.quickpoint.snookerboard.domain.utils.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.ui.navigation.MenuItem
import com.quickpoint.snookerboard.ui.navigation.Screen
import com.quickpoint.snookerboard.ui.navigation.getRouteFromMatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dataStore: DataStore
) : ViewModel() {

    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()
    fun turnOffSplashScreen() = viewModelScope.launch {
        delay(200)
        _keepSplashScreen.value = false
    }

    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()
    fun onEmit(screenEvent: ScreenEvents) = viewModelScope.launch {
        _eventSharedFlow.emit(screenEvent)
    }

    var cachedFrame: DomainFrame? = null
        private set

    fun loadMatchIfSaved() = viewModelScope.launch {
        dataStore.loadPreferences()
        DomainPlayer.Player01.assignDataStore(dataStore)
        DomainPlayer.Player02.assignDataStore(dataStore)
        Settings.dataStore = dataStore
        gameRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null) Settings.matchState = RULES_IDLE
            else cachedFrame = crtFrame.asDomain()
            onEmit(ScreenEvents.Navigate(getRouteFromMatchState(Settings.matchState)))
        }
    }

    // Repository
    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        gameRepository.deleteCrtFrame(Settings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        Settings.matchState = RULES_IDLE
        Settings.resetRules()
        gameRepository.deleteCrtMatch()
    }

    // AppBar
    private val _actionItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val actionItems = _actionItems.asStateFlow()
    private val _actionItemsOverflow = MutableStateFlow<List<MenuItem>>(emptyList())
    val actionItemsOverflow = _actionItemsOverflow.asStateFlow()
    private val _actionItemOnClick = MutableStateFlow<(MenuItem) -> Unit> {}
    val actionItemOnClick = _actionItemOnClick.asStateFlow()

    fun setupActionBarActions(actionItems: List<MenuItem>, actionItemsOverflow: List<MenuItem>, onMenuItemSelected: (MenuItem) -> Unit) =
        viewModelScope.launch {
            _actionItems.emit(actionItems)
            _actionItemsOverflow.emit(actionItemsOverflow)
            _actionItemOnClick.emit(onMenuItemSelected)
        }
}

fun MainViewModel.navigateToRulesScreen() {
    deleteMatchFromDb()
    onEmit(ScreenEvents.Navigate(Screen.Rules.route))
}