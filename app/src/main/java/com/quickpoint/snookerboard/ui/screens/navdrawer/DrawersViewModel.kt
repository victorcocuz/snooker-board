package com.quickpoint.snookerboard.ui.screens.navdrawer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.core.utils.vibrateOnce
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawersViewModel @Inject constructor(val dataStoreRepository: DataStoreRepository) : ViewModel() {
    fun onToggleChange(key: String, context: Context) {
        context.vibrateOnce()
        dataStoreRepository.switchBoolAndSavePref(key)
    }
}