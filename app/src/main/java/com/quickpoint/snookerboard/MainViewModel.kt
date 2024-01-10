package com.quickpoint.snookerboard

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.core.ScreenEvents
import com.quickpoint.snookerboard.data.DataStore
import com.quickpoint.snookerboard.data.K_INT_MATCH_AVAILABLE_FRAMES
import com.quickpoint.snookerboard.data.K_INT_MATCH_AVAILABLE_POINTS
import com.quickpoint.snookerboard.data.K_INT_MATCH_AVAILABLE_REDS
import com.quickpoint.snookerboard.data.K_INT_MATCH_COUNTER_RETAKE
import com.quickpoint.snookerboard.data.K_INT_MATCH_CRT_PLAYER
import com.quickpoint.snookerboard.data.K_INT_MATCH_FOUL_MODIFIER
import com.quickpoint.snookerboard.data.K_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.data.K_INT_MATCH_HANDICAP_MATCH
import com.quickpoint.snookerboard.data.K_INT_MATCH_POINTS_WITHOUT_RETURN
import com.quickpoint.snookerboard.data.K_INT_MATCH_STARTING_PLAYER
import com.quickpoint.snookerboard.data.K_INT_MATCH_UNIQUE_ID
import com.quickpoint.snookerboard.data.K_LONG_MATCH_CRT_FRAME
import com.quickpoint.snookerboard.data.K_LONG_MATCH_STATE
import com.quickpoint.snookerboard.data.database.models.asDomain
import com.quickpoint.snookerboard.domain.models.DomainFrame
import com.quickpoint.snookerboard.domain.repository.GameRepository
import com.quickpoint.snookerboard.domain.utils.DomainPlayer
import com.quickpoint.snookerboard.domain.utils.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.domain.utils.getMatchStateFromOrdinal
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
    private val dataStore: DataStore,
    private val matchSettings: MatchSettings
) : ViewModel() {

    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()

    fun turnOffSplashScreen() = viewModelScope.launch {
        delay(200)
        _keepSplashScreen.value = false
    }

    private val _screenEventAction = MutableSharedFlow<ScreenEvents>()
    val screenEventAction = _screenEventAction.asSharedFlow()
    fun onEmit(screenEvent: ScreenEvents) = viewModelScope.launch {
        _screenEventAction.emit(screenEvent)
    }

    var cachedFrame: DomainFrame? = null
        private set

    fun loadMatchIfSaved() = viewModelScope.launch {
        dataStore.loadPreferences()
        val preferences = dataStore.getPreferences()
        matchSettings.loadPreferences(  matchState = getMatchStateFromOrdinal(preferences[intPreferencesKey(K_LONG_MATCH_STATE)] ?: 0),
            uniqueId = preferences[longPreferencesKey(K_INT_MATCH_UNIQUE_ID)] ?: 0,
            availableFrames = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_FRAMES)] ?: 2,
            availableReds = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_REDS)] ?: 15,
            foulModifier = preferences[intPreferencesKey(K_INT_MATCH_FOUL_MODIFIER)] ?: 0,
            startingPlayer = preferences[intPreferencesKey(K_INT_MATCH_STARTING_PLAYER)] ?: -1,
            handicapFrame = preferences[intPreferencesKey(K_INT_MATCH_HANDICAP_FRAME)] ?: 0,
            handicapMatch = preferences[intPreferencesKey(K_INT_MATCH_HANDICAP_MATCH)] ?: 0,
            crtFrame = preferences[longPreferencesKey(K_LONG_MATCH_CRT_FRAME)] ?: 0L,
            crtPlayer = preferences[intPreferencesKey(K_INT_MATCH_CRT_PLAYER)] ?: -1,
            maxFramePoints = preferences[intPreferencesKey(K_INT_MATCH_AVAILABLE_POINTS)] ?: 0,
            counterRetake = preferences[intPreferencesKey(K_INT_MATCH_COUNTER_RETAKE)] ?: 0,
            pointsWithoutReturn = preferences[intPreferencesKey(K_INT_MATCH_POINTS_WITHOUT_RETURN)] ?: 0)
        DomainPlayer.Player01.assignDataStore(dataStore)
        DomainPlayer.Player02.assignDataStore(dataStore)
        gameRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null) MatchSettings.matchState = RULES_IDLE
            else cachedFrame = crtFrame.asDomain()
            onEmit(ScreenEvents.Navigate(getRouteFromMatchState(MatchSettings.matchState)))
        }
    }

    // Repository
    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        gameRepository.deleteCrtFrame(MatchSettings.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        MatchSettings.matchState = RULES_IDLE
        matchSettings.resetRules()
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