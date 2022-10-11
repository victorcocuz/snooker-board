package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo
import com.quickpoint.snookerboard.domain.DomainFreeBallInfo.*
import com.quickpoint.snookerboard.domain.DomainMatchInfo
import com.quickpoint.snookerboard.domain.DomainMatchInfo.*
import com.quickpoint.snookerboard.domain.DomainPlayer
import com.quickpoint.snookerboard.domain.DomainPlayer.*
import timber.log.Timber

// General
fun Fragment.toast(message: CharSequence) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
fun Application.toast(message: CharSequence) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

fun MenuItem.setStateOpacity() {
    icon.alpha = if (isEnabled) 255 else 120
    val s = SpannableString(title)
    s.setSpan(
        ForegroundColorSpan(
            if (isEnabled) Color.argb(255, 255, 255, 255)
            else Color.argb(120, 255, 255, 255)
        ), 0, s.length, 0
    )
    title = s
}

// Shared preferences
fun Fragment.getSharedPref(): SharedPreferences = this.requireActivity().getSharedPref()
fun Activity.getSharedPref(): SharedPreferences = application.getSharedPref()
fun Application.getSharedPref(): SharedPreferences = getSharedPreferences(
    getString(R.string.sp_file_key),
    Context.MODE_PRIVATE
)

fun SharedPreferences.setMatchInProgress(isInProgress: Boolean) {
    this.edit().putBoolean("isMatchInProgress", isInProgress).apply()
    Timber.i("setMatchInProgress() $isInProgress")
}


fun SharedPreferences.isMatchInProgress() = this.getBoolean("isMatchInProgress", false)
fun SharedPreferences.getCurrentFrame(app: Application) = this.getInt(app.getString(R.string.sp_match_frame_count), 0)
fun SharedPreferences.savePrefNames(application: Application) {
    edit().apply {
        application.apply {
            putString(getString(R.string.sp_match_name_first_a), PLAYER01.firstName)
            putString(getString(R.string.sp_match_name_last_a), PLAYER01.lastName)
            putString(getString(R.string.sp_match_name_first_b), PLAYER02.firstName)
            putString(getString(R.string.sp_match_name_last_b), PLAYER02.lastName)
            apply()
        }
        Timber.i("Add to sharedPref ${PLAYER01.getPlayerText()} and ${PLAYER02.getPlayerText()}")
    }
}

fun SharedPreferences.loadPrefNames(application: Application) {
    application.resources.apply {
        PLAYER01.firstName = getString(getString(R.string.sp_match_name_first_a), "")
        PLAYER01.lastName = getString(getString(R.string.sp_match_name_last_a), "")
        PLAYER02.firstName = getString(getString(R.string.sp_match_name_first_b), "")
        PLAYER02.lastName = getString(getString(R.string.sp_match_name_last_b), "")
    }
    Timber.i("Get from sharedPref ${PLAYER01.getPlayerText()} and ${PLAYER02.getPlayerText()}")
}

fun SharedPreferences.savePrefRulesAndFreeball(application: Application) {
    edit().apply {
        application.apply {
            putInt(getString(R.string.sp_match_frames), RULES.frames)
            putInt(getString(R.string.sp_match_reds), RULES.reds)
            putInt(getString(R.string.sp_match_foul), RULES.foul)
            putInt(getString(R.string.sp_match_first), RULES.first)
            putInt(getString(R.string.sp_match_crt_player), RULES.crtPlayer)
            putInt(getString(R.string.sp_match_frame_count), RULES.frameCount)
            putInt(getString(R.string.sp_match_frame_max), RULES.frameMax)
            putBoolean(getString(R.string.sp_match_freeball_visibility), FREEBALLINFO.isVisible)
            putBoolean(getString(R.string.sp_match_freeball_selection), FREEBALLINFO.isSelected)
            apply()
        }
        Timber.i("Add to sharedPref ${RULES.getRulesText()}")
        Timber.i("Add to sharedPref freeball isVisible ${FREEBALLINFO.isVisible} and isSelected ${FREEBALLINFO.isSelected}")
    }
}

fun SharedPreferences.loadPrefRulesAndFreeball(application: Application) {
    application.resources.apply {
        RULES.assignRules(
            getInt(getString(R.string.sp_match_frames), 3),
            getInt(getString(R.string.sp_match_reds), 15),
            getInt(getString(R.string.sp_match_foul), 0),
            getInt(getString(R.string.sp_match_first), 0),
            getInt(getString(R.string.sp_match_crt_player), 0),
            getInt(getString(R.string.sp_match_frame_count), 1),
            getInt(getString(R.string.sp_match_frame_max), 0)
        )
        FREEBALLINFO.assignFreeballInfo(
            getBoolean(getString(R.string.sp_match_freeball_visibility), false),
            getBoolean(getString(R.string.sp_match_freeball_selection), false)
        )
    }
    Timber.i("Get from sharedPref ${RULES.getRulesText()}")
    Timber.i("Get from sharedPref freeball isVisible ${FREEBALLINFO.isVisible} and isSelected ${FREEBALLINFO.isSelected}")

}

// Keyboard
fun Fragment.hideKeyboard() {
    view?.let {
        activity?.hideKeyboard(it)
    }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

// Dialog
fun DialogFragment.setLayoutSizeByFactor(factor: Float) {
    val width = Resources.getSystem().displayMetrics.widthPixels
    dialog?.window?.setLayout((width * factor).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
}

// Scrolling
fun ScrollView.assignScrollHeight(scrollHeight: Int, ghostHeight: Int) {
    val params = layoutParams
    if (scrollHeight > ghostHeight) {
        params.height = ghostHeight
    }
}

fun ScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}

fun Context.getFactoredDimen(factor: Int): Int {
    val width = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        resources.displayMetrics.widthPixels
    } else {
        resources.displayMetrics.heightPixels
    }
    return width / factor
}

// Binding
fun FragmentGameBinding.setQueryMatchVisibility() {
    fragGameLayoutBreak.root.visibility = View.GONE
    fragGameLayoutActionButtons.root.visibility = View.GONE
    fragGameLayoutQuery.root.visibility = View.VISIBLE
}

fun FragmentGameBinding.setPlayMatchVisibility() {
    fragGameLayoutBreak.root.visibility = View.VISIBLE
    fragGameLayoutActionButtons.root.visibility = View.VISIBLE
    fragGameLayoutQuery.root.visibility = View.GONE
}