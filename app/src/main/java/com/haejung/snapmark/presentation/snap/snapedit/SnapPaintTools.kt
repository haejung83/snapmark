package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.getBoundRectF
import com.haejung.snapmark.extend.px

class SnapPaintTools(context: Context) {

    private val _borderPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        strokeWidth = 1.px.toFloat()
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val _anchorPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        strokeWidth = 1.px.toFloat()
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    private val _helpToolBitmap =
        Bitmap.createBitmap(helpToolDiameter, helpToolDiameter, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).also {
                it.drawARGB(0, 0, 0, 0)
                it.drawOval(this.getBoundRectF(), anchorPaint)
            }
        }

    val helpToolBitmapOffset: Int
        get() = helpToolRadius

    val borderPaint: Paint
        get() = _borderPaint

    val anchorPaint: Paint
        get() = _anchorPaint

    val helpToolBitmap: Bitmap
        get() = _helpToolBitmap

    companion object {
        private val helpToolRadius = 6.px
        private val helpToolDiameter = helpToolRadius * 2
    }

}