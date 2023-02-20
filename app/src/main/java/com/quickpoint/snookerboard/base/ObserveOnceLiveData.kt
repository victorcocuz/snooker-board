package com.quickpoint.snookerboard.base

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
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