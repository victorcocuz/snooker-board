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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.quickpoint.snookerboard.R


fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//fun DialogFragment.setWidthPercent(percentage: Int) {
//    val percent = percentage.toFloat() / 100
//    val dm = Resources.getSystem().displayMetrics
//    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
//    val percentWidth = rect.width() * percent
//    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
//}

fun DialogFragment.setSize(factor: Float) {
    //    val outMetrics = Resources.getSystem().displayMetrics
    //    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
    //        val display = requireActivity().display
    //        display?.getRealMetrics(outMetrics)
    //    } else {
    //        @Suppress("DEPRECATION")
    //        val display = requireActivity().windowManager.defaultDisplay
    //        @Suppress("DEPRECATION")
    //        display.getMetrics(outMetrics)
    //    }

    val width = Resources.getSystem().displayMetrics.widthPixels
    dialog?.window?.setLayout((width * factor).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    //    window.setGravity(Gravity.CENTER)
}

fun DialogFragment.setFullScreen() {
    dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Activity.getSharedPref(): SharedPreferences = application.getSharedPref()
fun Application.getSharedPref(): SharedPreferences = getSharedPreferences(
    getString(R.string.preference_file_key),
    Context.MODE_PRIVATE
)

fun SharedPreferences.setMatchInProgress(isInProgress: Boolean) {
    this.edit().putBoolean("isMatchInProgress", isInProgress).apply()
}

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

fun MenuItem.setStateOpacity() {
    icon.alpha = if (isEnabled) 255 else 120
    val s = SpannableString(title)
    s.setSpan(ForegroundColorSpan(if (isEnabled) Color.argb(255, 255, 255, 255) else Color.argb(120, 255, 255, 255)), 0, s.length, 0)
    title = s
}

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