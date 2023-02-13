package com.quickpoint.snookerboard.base

// For more info visit: https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
}