package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.haejung.snapmark.R

class SnapPaintTools(context: Context) {

    private val _borderPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        strokeWidth = 4F
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val _anchorPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        strokeWidth = 4F
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    val borderPaint: Paint
        get() = _borderPaint

    val anchorPaint: Paint
        get() = _anchorPaint
}