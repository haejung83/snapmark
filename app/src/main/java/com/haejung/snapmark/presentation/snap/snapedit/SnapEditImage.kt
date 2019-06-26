package com.haejung.snapmark.presentation.snap.snapedit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.haejung.snapmark.extend.getBoundLinePointFloatArray
import com.haejung.snapmark.extend.getBoundRectF
import com.haejung.snapmark.extend.px
import timber.log.Timber
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min

class SnapEditImage(val image: Bitmap, private val window: RectF) {

    private val drawMatrix = Matrix()
    private val invertedDrawMatrix = Matrix()

    private val drawRectF = RectF()
    private val transformedDrawRectF = RectF()

    private val drawPoints = FloatArray(16)
    private val transformedDrawPoints = FloatArray(16)

    private val drawToolPoints = FloatArray(2)
    private val transformedDrawToolPoints = FloatArray(2)

    private val leftTopCornerBounds = RectF()
    private val rightTopCornerBounds = RectF()
    private val rightBottomCornerBounds = RectF()
    private val leftBottomCornerBounds = RectF()
    private val rotationBounds = RectF()
    private val centerBounds = RectF()

    private lateinit var cornerBounds: CornerBounds

    sealed class CornerBounds {
        object LeftTop : CornerBounds()
        object LeftBottom : CornerBounds()
        object RightTop : CornerBounds()
        object RightBottom : CornerBounds()
        object Rotation : CornerBounds()
        object Center : CornerBounds()
        object Outside : CornerBounds()
    }

    init {
        image.let { bitmap ->
            bitmap.getBoundRectF().let {
                Timber.i("Image BoundsRectF: ${it.toShortString()}")
                drawRectF.set(it)
                transformedDrawRectF.set(it)

                floatArrayOf(it.width() / 2, it.top - it.height() / 4).copyInto(drawToolPoints)
                drawToolPoints.copyInto(transformedDrawToolPoints)
            }

            bitmap.getBoundLinePointFloatArray().let {
                it.copyInto(drawPoints)
                it.copyInto(transformedDrawPoints)
            }

            val bitmapWidth = bitmap.width.toFloat()
            val scaleFactor = min(window.width() / 3F, bitmapWidth) / bitmapWidth
            scale(scaleFactor, scaleFactor, bitmap.width / 2F, bitmap.height / 2F)

            translate(
                window.width() / 2F - bitmap.width / 2F,
                window.height() / 2F - bitmap.height / 2F
            )

            // Update
            updateBoundsRegion(drawPoints, drawToolPoints)
            updateInvertedDrawMatrix()
            mapTransformedByDrawMatrix()
        }
    }

    private fun translate(dx: Float, dy: Float) {
        drawMatrix.postTranslate(dx, dy)
    }

    private fun scale(dx: Float, dy: Float, px: Float, py: Float) {
        val normalized = (dx + dy) / 2F
        drawMatrix.postScale(normalized, normalized, px, py)
    }

    private fun rotate(degree: Float, px: Float, py: Float) {
        drawMatrix.postRotate(degree, px, py)
    }

    private fun updateInvertedDrawMatrix() {
        invertedDrawMatrix.apply {
            drawMatrix.invert(this)
        }
    }

    private fun mapTransformedByDrawMatrix() {
        drawMatrix.mapRect(transformedDrawRectF, drawRectF)
        drawMatrix.mapPoints(transformedDrawPoints, drawPoints)
        drawMatrix.mapPoints(transformedDrawToolPoints, drawToolPoints)
    }

    fun actionStart(points: FloatArray) {
        updateInvertedDrawMatrix()

        // Invert points by inverted draw matrix
        val invertedPoints = points.copyOf()
        invertedDrawMatrix.mapPoints(invertedPoints)

        // Calculate corner of bounds
        cornerBounds = getCornerBoundsByPoints(invertedPoints)
        Timber.i("RegionOfRect: $cornerBounds")
    }

    private var rotateValue = 0.0

    fun actionMove(points: FloatArray, oldPoints: FloatArray): Boolean {
        if (cornerBounds == CornerBounds.Outside) return false

        val pointsForMapping = floatArrayOf(*points, *oldPoints)
        invertedDrawMatrix.mapPoints(pointsForMapping)

        val dScaleX = pointsForMapping[0] - pointsForMapping[2]
        val dScaleY = pointsForMapping[1] - pointsForMapping[3]

        when (cornerBounds) {
            CornerBounds.Center ->
                translate(points[0] - oldPoints[0], points[1] - oldPoints[1])
            CornerBounds.Rotation -> {
                val dpx = points[0] - transformedDrawRectF.centerX()
                val dpy = points[1] - transformedDrawRectF.centerY() + Float.MIN_VALUE
                var radian = atan2(dpx.toDouble(), -dpy.toDouble())
                if (radian < 0.0) radian += PI2
                val degree = radian * RAD2DEG
                Timber.i("Degree: $degree")

                val diffValue = degree - rotateValue
                rotate(diffValue.toFloat(), transformedDrawRectF.centerX(), transformedDrawRectF.centerY())
                rotateValue = degree
            }
            CornerBounds.RightBottom -> scale(
                1F + (2F * (dScaleX / drawRectF.width())),
                1F + (2F * (dScaleY / drawRectF.height())),
                transformedDrawRectF.centerX(),
                transformedDrawRectF.centerY()
            )
            CornerBounds.RightTop -> scale(
                1F + (2F * (dScaleX / drawRectF.width())),
                1F - (2F * (dScaleY / drawRectF.height())),
                transformedDrawRectF.centerX(),
                transformedDrawRectF.centerY()
            )
            CornerBounds.LeftBottom -> scale(
                1F - (2F * (dScaleX / drawRectF.width())),
                1F + (2F * (dScaleY / drawRectF.height())),
                transformedDrawRectF.centerX(),
                transformedDrawRectF.centerY()
            )
            CornerBounds.LeftTop -> scale(
                1F - (2F * (dScaleX / drawRectF.width())),
                1F - (2F * (dScaleY / drawRectF.height())),
                transformedDrawRectF.centerX(),
                transformedDrawRectF.centerY()
            )
        }

        updateInvertedDrawMatrix()
        mapTransformedByDrawMatrix()
        return true
    }

    fun onDraw(canvas: Canvas, paintTools: SnapPaintTools) {
        canvas.drawBitmap(image, drawMatrix, null)
        canvas.drawLines(transformedDrawPoints, paintTools.borderPaint)
        canvas.drawBitmap(
            paintTools.helpToolBitmap,
            transformedDrawPoints[0] - paintTools.helpToolBitmapOffset,
            transformedDrawPoints[1] - paintTools.helpToolBitmapOffset,
            null
        )
        canvas.drawBitmap(
            paintTools.helpToolBitmap,
            transformedDrawPoints[4] - paintTools.helpToolBitmapOffset,
            transformedDrawPoints[5] - paintTools.helpToolBitmapOffset,
            null
        )
        canvas.drawBitmap(
            paintTools.helpToolBitmap,
            transformedDrawPoints[8] - paintTools.helpToolBitmapOffset,
            transformedDrawPoints[9] - paintTools.helpToolBitmapOffset,
            null
        )
        canvas.drawBitmap(
            paintTools.helpToolBitmap,
            transformedDrawPoints[12] - paintTools.helpToolBitmapOffset,
            transformedDrawPoints[13] - paintTools.helpToolBitmapOffset,
            null
        )
        canvas.drawBitmap(
            paintTools.helpToolBitmap,
            transformedDrawToolPoints[0] - paintTools.helpToolBitmapOffset,
            transformedDrawToolPoints[1] - paintTools.helpToolBitmapOffset,
            null
        )
    }

    fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(image, drawMatrix, null)
    }

    private fun updateBoundsRegion(cornerBoundPoints: FloatArray, rotationBoundPoints: FloatArray) {
        with(cornerBoundPoints) {
            leftTopCornerBounds.set(
                this[0] - INBOUND,
                this[1] - INBOUND,
                this[0] + INBOUND,
                this[1] + INBOUND
            )
            rightTopCornerBounds.set(
                this[4] - INBOUND,
                this[5] - INBOUND,
                this[4] + INBOUND,
                this[5] + INBOUND
            )
            rightBottomCornerBounds.set(
                this[8] - INBOUND,
                this[9] - INBOUND,
                this[8] + INBOUND,
                this[9] + INBOUND
            )
            leftBottomCornerBounds.set(
                this[12] - INBOUND,
                this[13] - INBOUND,
                this[12] + INBOUND,
                this[13] + INBOUND
            )
            centerBounds.set(this[0], this[1], this[8], this[9])
        }
        with(rotationBoundPoints) {
            rotationBounds.set(
                this[0] - INBOUND,
                this[1] - INBOUND,
                this[0] + INBOUND,
                this[1] + INBOUND
            )
        }
    }

    private fun getCornerBoundsByPoints(points: FloatArray): CornerBounds {
        val x = points[0]
        val y = points[1]

        return when {
            leftTopCornerBounds.contains(x, y) -> CornerBounds.LeftTop
            leftBottomCornerBounds.contains(x, y) -> CornerBounds.LeftBottom
            rightTopCornerBounds.contains(x, y) -> CornerBounds.RightTop
            rightBottomCornerBounds.contains(x, y) -> CornerBounds.RightBottom
            rotationBounds.contains(x, y) -> CornerBounds.Rotation
            centerBounds.contains(x, y) -> CornerBounds.Center
            else -> CornerBounds.Outside
        }
    }

    companion object {
        private const val PI2: Double = PI * 2.0
        private const val RAD2DEG: Double = 180.0 / PI
        private val INBOUND: Float = 10.px.toFloat()
    }

}