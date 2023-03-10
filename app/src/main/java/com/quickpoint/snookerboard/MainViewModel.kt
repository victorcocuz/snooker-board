package com.quickpoint.snookerboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.database.models.asDomain
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.ui.navigation.MenuItem
import com.quickpoint.snookerboard.ui.navigation.Screen
import com.quickpoint.snookerboard.ui.navigation.getRouteFromMatchState
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(
    private val snookerRepository: SnookerRepository,
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
        snookerRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null) Settings.matchState = RULES_IDLE
            else cachedFrame = crtFrame.asDomain()
            onEmit(ScreenEvents.Navigate(getRouteFromMatchState(Settings.matchState)))
        }
    }

    // Repository
    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCrtFrame(Settings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        Settings.matchState = RULES_IDLE
        Settings.resetRules()
        snookerRepository.deleteCrtMatch()
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

    // Data Store
    val toggleAdvancedRules = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_ADVANCED_RULES)
    val toggleAdvancedStatistics = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_ADVANCED_STATISTICS)
    val toggleAdvancedBreaks = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_ADVANCED_BREAKS)
    val toggleLongShot = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_LONG_SHOT)
    val toggleRestShot = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_REST_SHOT)
    val toggleFreeball = dataStore.preferencesBooleanFlow(K_BOOL_TOGGLE_FREEBALL)
    fun savePref(key: String, value: Boolean) = dataStore.savePreferences(key, value)

}

fun MainViewModel.navigateToRulesScreen() {
    deleteMatchFromDb()
    onEmit(ScreenEvents.Navigate(Screen.Rules.route))
}