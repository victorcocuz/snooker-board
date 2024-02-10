package com.quickpoint.snookerboard.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.quickpoint.snookerboard.core.utils.Constants.EMAIL_BASE_URI


// General

fun Any.asText() = toString()
    .replace("DomainActionLog(description=", Constants.EMPTY_STRING)
    .removeSuffix(")")
    .split(", ")
    .filter { !it.contains("=null") }
    .toString()
    .removePrefix("[")
    .removeSuffix("]")


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

// Context
tailrec fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.getActivity()
}

fun Context.vibrateOnce() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vib  = vibratorManager.defaultVibrator
        vib.vibrate(VibrationEffect.createOneShot(50,1 ))
    } else {
        @Suppress("DEPRECATION")
        val vib  = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(50)
    }
}

fun Context.sendEmail(to: Array<String>, subject: String, body: String = Constants.EMPTY_STRING) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(EMAIL_BASE_URI)
        putExtra(Intent.EXTRA_EMAIL, to)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(packageManager) != null) startActivity(intent)
}

// Other
fun colorTransition(isActivePlayer: Boolean, @ColorRes endColor: Int, delay: Long = 200L) {
//todo  add color transition, revise this method
    var colorFrom = Color.TRANSPARENT
//    if (background is ColorDrawable)
//        colorFrom = (background as ColorDrawable).color
//    val colorTo = ContextCompat.getColor(context, endColor)
//    val colorAnimation: ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
//    colorAnimation.apply {
//        duration = delay
//        addUpdateListener {
//            if (it.animatedValue is Int) {
//                val color = it.animatedValue as Int
//                setBackgroundColor(color)
//            }
//        }
//        start()
//    }
//    for (i in 0 until childCount) {
//        getChildAt(i).animate().apply {
//            duration = delay
//            alpha(if (isActivePlayer) 1F else 0.5F)
//        }
//    }
}