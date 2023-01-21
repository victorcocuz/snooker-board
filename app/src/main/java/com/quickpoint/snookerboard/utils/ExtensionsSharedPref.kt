@file:Suppress("unused")

package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.SnookerBoardApplication
import com.quickpoint.snookerboard.domain.objects.MatchSettings.*

// Shared preferences
fun Fragment.sharedPref(): SharedPreferences = requireActivity().sharedPref()
fun Activity.sharedPref(): SharedPreferences = application.sharedPref()
fun Application.sharedPref(): SharedPreferences = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

fun SharedPreferences.updateState() =
    edit().putInt(SnookerBoardApplication.application().getString(R.string.sp_match_state), Settings.matchState.ordinal).apply()

fun SharedPreferences.savePref() {
//    edit().apply {
//        SnookerBoardApplication.application().apply {
//            putInt(getString(R.string.sp_match_state), Settings.matchState.ordinal)

            // Player
//            putString(getString(R.string.sp_match_name_first_a), PLAYER01.firstName)
//            putString(getString(R.string.sp_match_name_last_a), PLAYER01.lastName)
//            putString(getString(R.string.sp_match_name_first_b), PLAYER02.firstName)
//            putString(getString(R.string.sp_match_name_last_b), PLAYER02.lastName)

            // Settings
//            putLong(getString(R.string.sp_match_unique_id), Settings.uniqueId)
//            putInt(getString(R.string.sp_match_max_frames_available), Settings.availableFrames)
//            putInt(getString(R.string.sp_match_reds), Settings.availableReds)
//            putInt(getString(R.string.sp_match_foul), Settings.foulModifier)
//            putInt(getString(R.string.sp_match_first_player), Settings.startingPlayer)
//            putInt(getString(R.string.sp_match_crt_player), Settings.crtPlayer)
//            putLong(getString(R.string.sp_match_crt_frame), Settings.crtFrame)
//            putInt(getString(R.string.sp_match_max_available_points), Settings.availablePoints)
//            putInt(getString(R.string.sp_match_max_counter_retake), Settings.counterRetake)
//            putInt(getString(R.string.sp_match_handicap_frame), Settings.handicapFrame)
//            putInt(getString(R.string.sp_match_handicap_match), Settings.handicapMatch)
//            putInt(getString(R.string.sp_match_ongoing_points_without_return), Settings.ongoingPointsWithoutReturn)

            // Frame Toggles
//            putBoolean(getString(R.string.sp_match_toggle_freeball), FrameToggles.FRAMETOGGLES.isFreeball)
//            putBoolean(getString(R.string.sp_match_toggle_long), FrameToggles.FRAMETOGGLES.isLongShot)
//            putBoolean(getString(R.string.sp_match_toggle_rest), FrameToggles.FRAMETOGGLES.isRestShot)

            // Match Toggles
//            putBoolean(getString(R.string.sp_toggle_advanced_rules), Toggle.AdvancedRules.isEnabled)
//            putBoolean(getString(R.string.sp_toggle_advanced_statistics), Toggle.AdvancedStatistics.isEnabled)
//            putBoolean(getString(R.string.sp_toggle_advanced_breaks), Toggle.AdvancedBreaks.isEnabled)

//            apply()
//        }
//        Timber.i(
//            "-----------------------------------------ADD TO SHARED PREF---------------------------------------------\n" +
//                    "Player ${Settings.matchState}, ${Player01.getPlayerText()} and ${Player02.getPlayerText()}\n" +
//                    "${Settings.getAsText()}\n" +
//                    "${FrameToggles.FRAMETOGGLES.getAsText()}\n"
//        )
//    }
}

fun SharedPreferences.loadPref() {
//    SnookerBoardApplication.application().resources.apply {
//        Settings.getMatchStateFromOrdinal(getInt(getString(R.string.sp_match_state), 0))
//        PLAYER01.firstName = getString(getString(R.string.sp_match_name_first_a), if (BuildConfig.DEBUG_TOGGLE) "Ronnie" else "") ?: ""
//        PLAYER01.lastName = getString(getString(R.string.sp_match_name_last_a), if (BuildConfig.DEBUG_TOGGLE) "O'Sullivan" else "") ?: ""
//        PLAYER02.firstName = getString(getString(R.string.sp_match_name_first_b), if (BuildConfig.DEBUG_TOGGLE) "John" else "") ?: ""
//        PLAYER02.lastName = getString(getString(R.string.sp_match_name_last_b), if (BuildConfig.DEBUG_TOGGLE) "Higgins" else "") ?: ""

//        Settings.assignRules(
//            getLong(getString(R.string.sp_match_unique_id), -1),
//            getInt(getString(R.string.sp_match_max_frames_available), 3),
//            getInt(getString(R.string.sp_match_reds), 15),
//            getInt(getString(R.string.sp_match_foul), 0),
//            getInt(getString(R.string.sp_match_first_player), 0),
//            getInt(getString(R.string.sp_match_crt_player), 0),
//            getLong(getString(R.string.sp_match_crt_frame), 1),
//            getInt(getString(R.string.sp_match_max_available_points), 0),
//            getInt(getString(R.string.sp_match_max_counter_retake), 0),
//            getInt(getString(R.string.sp_match_handicap_frame), 0),
//            getInt(getString(R.string.sp_match_handicap_match), 0),
//            getInt(getString(R.string.sp_match_ongoing_points_without_return), 0),
//        )

//        FrameToggles.FRAMETOGGLES.assignFrameToggles(
//            getBoolean(getString(R.string.sp_match_toggle_freeball), false),
//            getBoolean(getString(R.string.sp_match_toggle_long), false),
//            getBoolean(getString(R.string.sp_match_toggle_rest), false),
//        )

//        Toggle.AdvancedRules.isEnabled = getBoolean(getString(R.string.sp_toggle_advanced_rules), true)
//        Toggle.AdvancedStatistics.isEnabled = getBoolean(getString(R.string.sp_toggle_advanced_statistics), true)
//        Toggle.AdvancedBreaks.isEnabled = getBoolean(getString(R.string.sp_toggle_advanced_breaks), true)
//    }
//    Timber.i(
//        "----------------------------------------GET FROM SHARED PREF--------------------------------------------\n" +
//                "Player ${Settings.matchState}, ${Player01.getPlayerText()} and ${Player02.getPlayerText()}\n" +
//                "${Settings.getAsText()}\n" +
//                "${FrameToggles.FRAMETOGGLES.getAsText()}\n"
//    )

}
