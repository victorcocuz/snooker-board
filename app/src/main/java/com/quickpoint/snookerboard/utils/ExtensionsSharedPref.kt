@file:Suppress("unused")

package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.SnookerBoardApplication
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.FREEBALLINFO
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER01
import com.quickpoint.snookerboard.domain.DomainPlayer.PLAYER02
import com.quickpoint.snookerboard.utils.MatchSettings.*
import timber.log.Timber

// Shared preferences
fun Fragment.sharedPref(): SharedPreferences = requireActivity().sharedPref()
fun Activity.sharedPref(): SharedPreferences = application.sharedPref()
fun Application.sharedPref(): SharedPreferences = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

fun SharedPreferences.updateState() =
    edit().putInt(SnookerBoardApplication.application().getString(R.string.sp_match_state), SETTINGS.matchState.ordinal).apply()

fun SharedPreferences.savePref() {
    edit().apply {
        SnookerBoardApplication.application().apply {
            putInt(getString(R.string.sp_match_state), SETTINGS.matchState.ordinal)
            putString(getString(R.string.sp_match_name_first_a), PLAYER01.firstName)
            putString(getString(R.string.sp_match_name_last_a), PLAYER01.lastName)
            putString(getString(R.string.sp_match_name_first_b), PLAYER02.firstName)
            putString(getString(R.string.sp_match_name_last_b), PLAYER02.lastName)
            putLong(getString(R.string.sp_match_unique_id), SETTINGS.uniqueId)
            putInt(getString(R.string.sp_match_max_frames_available), SETTINGS.maxFramesAvailable)
            putInt(getString(R.string.sp_match_reds), SETTINGS.reds)
            putInt(getString(R.string.sp_match_foul), SETTINGS.foul)
            putInt(getString(R.string.sp_match_first_player), SETTINGS.firstPlayer)
            putInt(getString(R.string.sp_match_crt_player), SETTINGS.crtPlayer)
            putLong(getString(R.string.sp_match_crt_frame), SETTINGS.crtFrame)
            putInt(getString(R.string.sp_match_max_available_points), SETTINGS.maxAvailablePoints)
            putInt(getString(R.string.sp_match_max_counter_retake), SETTINGS.counterRetake)
            putBoolean(getString(R.string.sp_match_freeball_visibility), FREEBALLINFO.isVisible)
            putBoolean(getString(R.string.sp_match_freeball_selection), FREEBALLINFO.isSelected)
            apply()
        }
        Timber.i("Add to sharedPref ${SETTINGS.matchState}, ${PLAYER01.getPlayerText()} and ${PLAYER02.getPlayerText()}")
        Timber.i("Add to sharedPref ${SETTINGS.getAsText()}")
        Timber.i("Add to sharedPref freeball isVisible ${FREEBALLINFO.isVisible} and isSelected ${FREEBALLINFO.isSelected}")
    }
}

fun SharedPreferences.loadPref() {
    SnookerBoardApplication.application().resources.apply {
        SETTINGS.getMatchStateFromOrdinal(getInt(getString(R.string.sp_match_state), 0))
        PLAYER01.firstName = getString(getString(R.string.sp_match_name_first_a), if (BuildConfig.DEBUG_TOGGLE) "Ronnie" else "")
        PLAYER01.lastName = getString(getString(R.string.sp_match_name_last_a), if (BuildConfig.DEBUG_TOGGLE) "O'Sullivan" else "")
        PLAYER02.firstName = getString(getString(R.string.sp_match_name_first_b), if (BuildConfig.DEBUG_TOGGLE)  "John" else "")
        PLAYER02.lastName = getString(getString(R.string.sp_match_name_last_b), if (BuildConfig.DEBUG_TOGGLE)  "Higgins" else "")
        SETTINGS.assignRules(
            getLong(getString(R.string.sp_match_unique_id), -1),
            getInt(getString(R.string.sp_match_max_frames_available), 3),
            getInt(getString(R.string.sp_match_reds), 15),
            getInt(getString(R.string.sp_match_foul), 0),
            getInt(getString(R.string.sp_match_first_player), 0),
            getInt(getString(R.string.sp_match_crt_player), 0),
            getLong(getString(R.string.sp_match_crt_frame), 1),
            getInt(getString(R.string.sp_match_max_available_points), 0),
            getInt(getString(R.string.sp_match_max_counter_retake), 0),
        )
        FREEBALLINFO.assignFreeballInfo(getBoolean(getString(R.string.sp_match_freeball_visibility), false),
            getBoolean(getString(R.string.sp_match_freeball_selection), false))
    }
    Timber.i("Get from sharedPref ${SETTINGS.matchState}, ${PLAYER01.getPlayerText()} and ${PLAYER02.getPlayerText()}")
    Timber.i("Get from sharedPref ${SETTINGS.getAsText()}")
    Timber.i("Get from sharedPref freeball isVisible ${FREEBALLINFO.isVisible} and isSelected ${FREEBALLINFO.isSelected}")

}
