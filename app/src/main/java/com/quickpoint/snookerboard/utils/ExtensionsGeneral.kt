package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.quickpoint.snookerboard.databinding.FragmentGameBinding

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