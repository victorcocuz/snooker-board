package com.example.snookerscore.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.snookerscore.R

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

fun Activity.getSharedPref() = application.getSharedPreferences(
    getString(R.string.preference_file_key),
    Context.MODE_PRIVATE
)