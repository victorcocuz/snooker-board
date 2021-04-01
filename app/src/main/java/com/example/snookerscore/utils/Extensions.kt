package com.example.snookerscore.utils

import android.content.Context
import android.widget.Toast

class Extensions {
    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}