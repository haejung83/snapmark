package com.haejung.snapmark.presentation.snap.snapedit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.haejung.snapmark.extend.getBoundRectF

class SnapAutoScaleImage(image: Bitmap, window: RectF) : SnapImage(image, window) {

    private val drawRectF = RectF()

    init {
        drawRectF.set(getCenterFitRectF(image.getBoundRectF(), window))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onDraw(canvas: Canvas, paintTools: SnapPaintTools) {
        canvas.drawBitmap(image, null, drawRectF, null)
    }

    fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(image, null, drawRectF, null)
    }

    private fun getCenterFitRectF(src: RectF, dest: RectF): RectF {
        val width: Float
        val height: Float

        val widthRatio = src.width() / dest.width()
        val heightRatio = src.height() / dest.height()

        if (widthRatio >= heightRatio) {
            // W
            width = dest.width()
            height = src.height() * (width / src.width())
        } else {
            // H
            height = dest.height()
            width = src.width() * (height / src.height())
        }

        val xCenter = dest.centerX()
        val yCenter = dest.centerY()
        val halfWidth = width / 2F
        val halfHeight = height / 2F

        return RectF(xCenter - halfWidth, yCenter - halfHeight, xCenter + halfWidth, yCenter + halfHeight)
    }

}