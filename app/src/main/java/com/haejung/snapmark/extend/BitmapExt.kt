package com.haejung.snapmark.extend

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF

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

fun Bitmap.getBoundRect(): Rect = Rect(0, 0, width, height)

fun Bitmap.getBoundRectF(): RectF = RectF(0F, 0F, width.toFloat(), height.toFloat())

fun Bitmap.getBoundPointArray(): IntArray {
    val l = 0
    val t = 0
    val r = width
    val b = height
    return intArrayOf(l, t, r, t, r, b, l, b)
}

fun Bitmap.getBoundPointFloatArray(): FloatArray =
    getBoundPointArray().map { it.toFloat() }.toFloatArray()

fun Bitmap.getBoundLinePointArray(): IntArray {
    val l = 0
    val t = 0
    val r = width
    val b = height
    return intArrayOf(l, t, r, t, r, t, r, b, r, b, l, b, l, b, l, t)
}

fun Bitmap.getBoundLinePointFloatArray(): FloatArray =
    getBoundLinePointArray().map { it.toFloat() }.toFloatArray()

