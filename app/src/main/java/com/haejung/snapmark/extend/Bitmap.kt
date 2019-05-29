package com.haejung.snapmark.extend

import android.graphics.Bitmap

fun Bitmap.createScaledBitmapWithAspectRatio(bothMax: Int): Bitmap {
    val newWidth: Int
    val newHeight: Int

    if (this.width >= this.height) {
        val ratio: Float = this.width.toFloat() / this.height.toFloat()
        newWidth = bothMax
        newHeight = Math.round(bothMax / ratio)
    } else {
        val ratio: Float = this.height.toFloat() / this.width.toFloat()
        newWidth = Math.round(bothMax / ratio)
        newHeight = bothMax
    }

    return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
}