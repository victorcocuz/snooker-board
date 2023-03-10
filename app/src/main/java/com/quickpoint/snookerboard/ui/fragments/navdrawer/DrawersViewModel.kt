package com.quickpoint.snookerboard.ui.fragments.navdrawer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.utils.DataStore
import com.quickpoint.snookerboard.utils.vibrateOnce

class DrawersViewModel(private val dataStore: DataStore) : ViewModel() {
    fun onToggleChange(key: String, context: Context) {
        context.vibrateOnce()
        dataStore.saveAndSwitchValue(key)
    }
}