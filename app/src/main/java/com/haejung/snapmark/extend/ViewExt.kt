package com.haejung.snapmark.extend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: String, duration: Int) =
    Snackbar.make(this, message, duration).show()

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)
