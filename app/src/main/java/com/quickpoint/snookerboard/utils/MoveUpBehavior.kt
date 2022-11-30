package com.quickpoint.snookerboard.utils

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * To use this, the parent container must be a CoordinatorLayout.
 * This can be applied to a child ViewGroup with app:layout_behavior="com.quickpoint.snookerboard.utils.MoveUpwardBehavior"
 */
@Keep
class MoveUpwardBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs), Parcelable {

    private var originalPadding = -1

    constructor(parcel: Parcel) : this(
        TODO("context"),
        TODO("attrs")
    )

    override fun layoutDependsOn(parent: CoordinatorLayout, targetView: View, snackBar: View): Boolean {
        return snackBar is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, targetView: View, snackBar: View): Boolean {

        if (originalPadding==-1) {
            originalPadding = targetView.paddingBottom
            return true
        }
        val bottomPadding = min(0f, snackBar.translationY - snackBar.height).roundToInt()

        //Dismiss last SnackBar immediately to prevent from conflict when showing SnackBars immediately after each other
        ViewCompat.animate(targetView).cancel()

        //Set bottom padding so the target ViewGroup is not hidden
        targetView.setPadding(targetView.paddingLeft, targetView.paddingTop, targetView.paddingRight, -(bottomPadding-originalPadding))

        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, targetView: View, snackBar: View) {
        //Reset padding to default value
        targetView.setPadding(targetView.paddingLeft, targetView.paddingTop, targetView.paddingRight, originalPadding)
        originalPadding = -1
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoveUpwardBehavior> {
        override fun createFromParcel(parcel: Parcel): MoveUpwardBehavior {
            return MoveUpwardBehavior(parcel)
        }

        override fun newArray(size: Int): Array<MoveUpwardBehavior?> {
            return arrayOfNulls(size)
        }
    }
}