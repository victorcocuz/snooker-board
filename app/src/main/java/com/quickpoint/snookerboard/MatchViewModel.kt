package com.quickpoint.snookerboard

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.quickpoint.snookerboard.database.asDomainFrame
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchSettings.SETTINGS
import com.quickpoint.snookerboard.utils.MatchState.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class MatchViewModel(
    private val app: Application,
    private val snookerRepository: SnookerRepository,
) : AndroidViewModel(app) {

    // Observables
    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()
    fun transitionToFragment(fragment: Fragment, ms: Long) = viewModelScope.launch {
        fragment.startPostponedEnterTransition()
        delay(ms)
        _keepSplashScreen.value = false
        Timber.i("transitionToFragment(): ${fragment.javaClass.simpleName}")
    }

    private val _matchState = MutableLiveData<MatchState>()
    val matchState: LiveData<MatchState> = _matchState
    fun updateState(matchState: MatchState) = app.getSharedPref().apply {
        if (matchState == NONE) loadPref() else SETTINGS.setMatchState(matchState)
        if (matchState == SAVED) savePref() else updateState()
        if (matchState != NONE) _matchState.value = SETTINGS.matchState
    }

    // Separate threads
    private val _storedFrame = MutableLiveData<Event<DomainFrame>>()
    val storedFrame: LiveData<Event<DomainFrame>> = _storedFrame
    fun loadMatchIfSaved() = viewModelScope.launch { // If db is empty reset rules, otherwise load the most recent frame
        Timber.i("loadMatchIfSaved()")
        updateState(NONE) // Load shared preferences
        snookerRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null || (SETTINGS.matchState != SAVED && SETTINGS.matchState != POST_MATCH)) deleteMatchFromDb()
            else {
                _storedFrame.value = Event(crtFrame.asDomainFrame())
                 updateState(if(SETTINGS.matchState == SAVED) IN_PROGRESS else POST_MATCH)
            }
        }
    }

    fun deleteCrtFrameFromDb() = viewModelScope.launch {
        snookerRepository.deleteCurrentFrame(SETTINGS.crtFrame)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        updateState(IDLE)
        snookerRepository.deleteCurrentMatch()
    }

    fun emailLogs() = viewModelScope.launch {
        val logs = snookerRepository.getDebugFrameActionList().toString()
        val json = Gson().toJson(snookerRepository.getDebugFrameActionList())
        val body = "${SETTINGS.getAsText()} \n\n $json \n\n $logs"
        Timber.e(json)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(EMAIL_URI)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.ADMIN_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_LOGS_SUBJECT)
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_LOGS_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(app.packageManager) != null) app.applicationContext.startActivity(intent)
    }
}