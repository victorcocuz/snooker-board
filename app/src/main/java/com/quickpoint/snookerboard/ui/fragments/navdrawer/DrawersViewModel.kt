package com.quickpoint.snookerboard.ui.fragments.navdrawer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.objects.Toggle
import com.quickpoint.snookerboard.utils.*

class DrawersViewModel(private val dataStore: DataStore) : ViewModel() {
    fun onToggleChange(key: String?, context: Context) { // Update toggles, save changes in DataStore and notify the composable to recompose
        context.vibrateOnce()
        when (key) {
            K_BOOL_TOGGLE_ADVANCED_RULES -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_BOOL_TOGGLE_ADVANCED_STATISTICS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
            K_BOOL_TOGGLE_ADVANCED_BREAKS -> dataStore.savePreferences(key, Toggle.AdvancedRules.toggleEnabled())
        }
        _eventToggleChange.value = Event(Unit)
    }

    private val _eventToggleChange = MutableLiveData<Event<Unit>>()
    val eventToggleChange: LiveData<Event<Unit>> = _eventToggleChange
}