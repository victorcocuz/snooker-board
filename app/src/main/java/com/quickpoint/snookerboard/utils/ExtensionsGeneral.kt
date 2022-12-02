package com.quickpoint.snookerboard.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
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


// General
fun Fragment.navigate(directions: NavDirections, adMob: AdMob? = null) {
    findNavController().navigate(directions)
}

fun Fragment.toast(message: CharSequence) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
fun Activity.toast(message: CharSequence) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
fun View.snackbar(message: CharSequence) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackbar.isGestureInsetBottomIgnored = true
    snackbar.show()
}

fun FragmentGameBinding.snackbar(message: CharSequence) = fGameCdl.snackbar(message)
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

// Other
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