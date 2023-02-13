package com.quickpoint.snookerboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.database.models.asDomain
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.MatchState.RULES_IDLE
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.ui.navigation.getRouteFromMatchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(
    private val snookerRepository: SnookerRepository
) : ViewModel() {

    fun turnOffSplashScreen(msDelay: Long = 0) = viewModelScope.launch {
        delay(msDelay)
        _keepSplashScreen.value = false
    }
    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()

    fun loadMatchIfSaved() = viewModelScope.launch {
        snookerRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null) Settings.matchState = RULES_IDLE // Reset the app when something went wrong
            else _eventStoredFrame.emit(Event(crtFrame.asDomain()))
            Settings.matchState = RULES_IDLE // Temp
            onEmit(ScreenEvents.Navigate(getRouteFromMatchState(Settings.matchState)))
        }
    }
    private val _eventStoredFrame = MutableSharedFlow<Event<DomainFrame>>()
    val eventStoredFrame = _eventStoredFrame.asSharedFlow()

    fun onEmit(screenEvent: ScreenEvents) = viewModelScope.launch {
        _eventSharedFlow.emit(screenEvent)
    }
    private val _eventSharedFlow = MutableSharedFlow<ScreenEvents>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()
}