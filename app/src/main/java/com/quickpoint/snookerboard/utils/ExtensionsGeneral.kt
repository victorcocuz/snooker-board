package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.quickpoint.snookerboard.admob.AdMob
import com.quickpoint.snookerboard.databinding.FragmentGameBinding


// General
fun Fragment.navigate(directions: NavDirections, adMob: AdMob? = null) {
    findNavController().navigate(directions)
    adMob?.showInterstitialAd()
}
fun Fragment.toast(message: CharSequence) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
fun Activity.toast(message: CharSequence) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
fun View.snackbar(message: CharSequence) = Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
fun FragmentGameBinding.snackbar(message: CharSequence) = this.fGameCdl.snackbar(message)
fun Any?.asText() = if (this != null) ", ${this.javaClass.simpleName}: $this" else ""

fun MenuItem.setItemActive(isEnabled: Boolean) {
    icon?.alpha = if (isEnabled) 255 else 120
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

fun Context.getFactoredDimen(factor: Int): Int {
    val width = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        resources.displayMetrics.widthPixels
    } else {
        resources.displayMetrics.heightPixels
    }
    return width / factor
}

// Text
fun TextView.setAsLink() {
    movementMethod = LinkMovementMethod.getInstance()
    val spannable = SpannableString(text)
    for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(u.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
    }
    text = spannable
}

// Context
fun Context.lifecycleOwner(): LifecycleOwner? {
    var curContext = this
    var maxDepth = 20
    while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
        curContext = (curContext as ContextWrapper).baseContext
    }
    return if (curContext is LifecycleOwner) curContext
    else null
}

tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun View.removeFocusAndHideKeyboard(context: Context, event: MotionEvent) {
    if (event.action == MotionEvent.ACTION_DOWN) {
        if (this is EditText) {
            val outRect = Rect()
            getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                clearFocus()
                val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(getWindowToken(), 0)
            }
        }
    }
}