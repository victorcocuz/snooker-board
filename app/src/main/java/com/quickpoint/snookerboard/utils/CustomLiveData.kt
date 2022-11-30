@file:Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate",
    "MemberVisibilityCanBePrivate"
)

package com.quickpoint.snookerboard.utils

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import java.util.*

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}


/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

// Observe once function
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

class ValueKeeperLiveData<T> : MutableLiveData<T>() {

    private val queuedValues: Queue<T> = LinkedList()

    @Synchronized
    override fun postValue(value: T) {
        // We queue the value to ensure it is delivered
        // even if several ones are posted right after.
        // Then we call the base, which will eventually
        // call setValue().
        if (value != null) queuedValues.offer(value)
        super.postValue(value)
    }

    @MainThread
    @Synchronized
    override fun setValue(value: T) {
        // We first try to remove the value from the queue just
        // in case this line was reached from postValue(),
        // otherwise we will have it duplicated in the queue.
        queuedValues.remove(value)

        // We queue the new value and finally deliver the
        // entire queue of values to the observers.
        if (value != null) queuedValues.offer(value)
        while (!queuedValues.isEmpty())
            super.setValue(queuedValues.poll())
    }
}

fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T>Fragment.getNavigationResult(@IdRes id: Int?, key: String, onResult: (result: T) -> Unit) {
    val navBackStackEntry = findNavController().currentBackStackEntry
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            if (navBackStackEntry?.savedStateHandle?.contains(key) == true) {
                val result = navBackStackEntry.savedStateHandle.get<T>(key)
                result?.let(onResult)
                navBackStackEntry.savedStateHandle.remove<T>(key)
            }
        }
    }
    navBackStackEntry?.lifecycle?.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry?.lifecycle?.removeObserver(observer)
        }
    })
}