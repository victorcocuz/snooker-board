package com.quickpoint.snookerboard.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.admob.AdMob
import com.quickpoint.snookerboard.databinding.FragmentGameBinding
import com.quickpoint.snookerboard.databinding.FragmentRulesBinding


// General
fun Fragment.navigate(directions: NavDirections, adMob: AdMob? = null) {
    findNavController().navigate(directions)
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Fragment.toast(message: CharSequence) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
fun Activity.toast(message: CharSequence) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
fun View.snackbar(matchAction: MatchAction) {
    val snackbar = Snackbar.make(this, matchAction.getSnackText(context), Snackbar.LENGTH_LONG)
    snackbar.isGestureInsetBottomIgnored = true
    snackbar.show()
}
fun FragmentGameBinding.snackbar(matchAction: MatchAction) = fGameCdl.snackbar(matchAction)
fun FragmentRulesBinding.snackbar(matchAction: MatchAction) = fRulesCdl.snackbar(matchAction)

fun Any.asText() = toString()
    .replace("DomainActionLog(description=", "")
    .removeSuffix(")")
    .split(", ")
    .filter { !it.contains("=null") }
    .toString()
    .removePrefix("[")
    .removeSuffix("]")

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

fun MenuItem.onMenuItemLongClickListener(menu: Menu, function: () -> (Unit)) {
    if (itemId != R.id.menu_item_more) {
        setActionView(R.layout.item_action_button)
        actionView?.findViewById<ImageButton>(R.id.i_action_button)?.setImageDrawable(icon)
        actionView?.setOnLongClickListener {
            function()
            true
        }
        actionView?.setOnClickListener { menu.performIdentifierAction(itemId, 0) }
    }
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

fun Context.vibrateOnce() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =  this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vib  = vibratorManager.defaultVibrator
        vib.vibrate(VibrationEffect.createOneShot(50,1 ))
    } else {
        @Suppress("DEPRECATION")
        val vib  = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(50)
    }
}

fun Context.sendEmail(to: Array<String>, subject: String, body: String = "") {
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

fun LinearLayout.colorTransition(isActivePlayer: Boolean, @ColorRes endColor: Int, delay: Long = 200L) {
    var colorFrom = Color.TRANSPARENT
    if (background is ColorDrawable)
        colorFrom = (background as ColorDrawable).color
    val colorTo = ContextCompat.getColor(context, endColor)
    val colorAnimation: ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.apply {
        duration = delay
        addUpdateListener {
            if (it.animatedValue is Int) {
                val color = it.animatedValue as Int
                setBackgroundColor(color)
            }
        }
        start()
    }
    for (i in 0 until childCount) {
        getChildAt(i).animate().apply {
            duration = delay
            alpha(if (isActivePlayer) 1F else 0.5F)
        }
    }
}