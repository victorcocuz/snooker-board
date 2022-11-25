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
import com.quickpoint.snookerboard.utils.MatchRules.RULES
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

    private val jobQueue = JobQueue()

    // Observables
    private val _keepSplashScreen = MutableStateFlow(true)
    val keepSplashScreen = _keepSplashScreen.asStateFlow()
    fun transitionToFragment(fragment: Fragment, ms: Long) = viewModelScope.launch {
        fragment.startPostponedEnterTransition()
        delay(ms)
        _keepSplashScreen.value = false
        Timber.i("transitionToFragment(): ${fragment.javaClass.simpleName}")
    }

    private val _eventRules = MutableLiveData<RULES>()
    val eventRules: LiveData<RULES> = _eventRules
    fun updateRules(_unused: Int) {
        _eventRules.value = RULES
    }

    fun updateState(matchState: MatchState) = app.getSharedPref().apply {
        if (matchState == NONE) loadPref() else RULES.setMatchState(matchState)
        if (matchState == SAVED) {
            savePref()
            jobQueue.cancel()
        } else updateState()
        if (matchState != NONE) updateRules(-1)
    }

    // Separate threads
    private val _eventMatchAction = MutableLiveData<Event<MatchAction?>>()
    val eventMatchAction: LiveData<Event<MatchAction?>> = _eventMatchAction
    fun onEventMatchAction(matchAction: MatchAction?) = jobQueue.submit {
        Timber.e("onEventMatchAction $matchAction")
        _eventMatchAction.value = Event(matchAction)
    }

    fun loadMatchIfSaved() = viewModelScope.launch { // If db is empty reset rules, otherwise load the most recent frame
        updateState(NONE) // Load shared preferences
        snookerRepository.getCrtFrame().let { crtFrame ->
            if (crtFrame == null || (RULES.matchState != SAVED && RULES.matchState != POST_MATCH)) {
                deleteMatchFromDb()
            } else updateFrame(crtFrame.asDomainFrame())
            Timber.i("State is: ${RULES.matchState}, CrtFrame is: ${crtFrame?.frame?.frameId}, frameCount is: ${RULES.frameCount}")
        }
    }

    private val _displayFrame = MutableLiveData<DomainFrame>()
    val displayFrame: LiveData<DomainFrame> = _displayFrame
    fun updateFrame(domainFrame: DomainFrame) = jobQueue.submit {
        _displayFrame.postValue(domainFrame)
        if (RULES.matchState == IN_PROGRESS) snookerRepository.saveCurrentFrame(domainFrame)
        else if (RULES.matchState == SAVED || RULES.matchState == IDLE) updateState(IN_PROGRESS)
        updateRules(-1)
        Timber.i(app.resources.getString(R.string.helper_update_frame_info))
    }

    fun deleteCrtFrameFromDb() = jobQueue.submit {
       snookerRepository.deleteCurrentFrame(RULES.frameCount)
    }

    fun deleteMatchFromDb() = viewModelScope.launch { // When starting a new match or cancelling an existing match
        updateState(IDLE)
        snookerRepository.deleteCurrentMatch()
    }

    fun emailLogs() = viewModelScope.launch {
        val logs = snookerRepository.getDebugFrameActionList().toString()
        val json = Gson().toJson(snookerRepository.getDebugFrameActionList())

        val inputStream = javaClass.classLoader?.getResourceAsStream("kjjhtext.txt")
        val inputAsString = inputStream?.bufferedReader().use { it?.readText() ?: "" }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(EMAIL_URI)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.ADMIN_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_LOGS_SUBJECT)
            putExtra(Intent.EXTRA_SUBJECT, EMAIL_LOGS_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, logs)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(app.packageManager) != null) app.applicationContext.startActivity(intent)
        Timber.e(json)
    }
}