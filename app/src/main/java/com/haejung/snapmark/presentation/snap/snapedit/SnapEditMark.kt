package com.haejung.snapmark.presentation.snap.snapedit

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.haejung.snapmark.data.Mark
import timber.log.Timber

class SnapEditMark(val mark: Mark, val window: RectF) {

    private val drawMatrix = Matrix()
    private val invertedDrawMatrix = Matrix()

    private val drawRectF = RectF()
    private val transformedDrawRectF = RectF()

    private val drawPoints = FloatArray(16)
    private val transformedDrawPoints = FloatArray(16)

    private val leftTopCornerBounds = RectF()
    private val transformedLeftTopCornerBounds = RectF()
    private val rightTopCornerBounds = RectF()
    private val transformedRightTopCornerBounds = RectF()
    private val rightBottomCornerBounds = RectF()
    private val transformedRightBottomCornerBounds = RectF()
    private val leftBottomCornerBounds = RectF()
    private val transformedLeftBottomCornerBounds = RectF()

    private lateinit var cornerBounds: CornerBounds

    sealed class CornerBounds {
        object LeftTop : CornerBounds()
        object LeftBottom : CornerBounds()
        object RightTop : CornerBounds()
        object RightBottom : CornerBounds()
        object Center : CornerBounds()
        object Outside : CornerBounds()
    }

    init {
        setup()
    }

    private fun setup() {
        mark.image.let {
            Timber.i("Image Size: ${it.width}, ${it.height}")

            drawRectF.set(0F, 0F, it.width.toFloat(), it.height.toFloat())
            transformedDrawRectF.set(drawRectF)

            drawPoints[0] = drawRectF.left
            drawPoints[1] = drawRectF.top
            drawPoints[2] = drawRectF.right
            drawPoints[3] = drawRectF.top
            drawPoints[4] = drawRectF.right
            drawPoints[5] = drawRectF.top
            drawPoints[6] = drawRectF.right
            drawPoints[7] = drawRectF.bottom
            drawPoints[8] = drawRectF.right
            drawPoints[9] = drawRectF.bottom
            drawPoints[10] = drawRectF.left
            drawPoints[11] = drawRectF.bottom
            drawPoints[12] = drawRectF.left
            drawPoints[13] = drawRectF.bottom
            drawPoints[14] = drawRectF.left
            drawPoints[15] = drawRectF.top
            drawPoints.copyInto(transformedDrawPoints)

            translate(
                window.width() / 2F - it.width / 2F,
                window.height() / 2F - it.height / 2F
            )

            updateInvertedDrawMatrix()
            drawMatrix.mapPoints(transformedDrawPoints, drawPoints)
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

    fun actionStart(points: FloatArray) {
        // Update inverted matrix
        updateInvertedDrawMatrix()
        drawMatrix.mapRect(transformedDrawRectF, drawRectF)

        // Invert points by inverted draw matrix
        val invertedPoints = points.copyOf()
        invertedDrawMatrix.mapPoints(invertedPoints)

        // Calculate corner of bounds
        cornerBounds = getCornerBoundsByPoints(drawPoints, invertedPoints)
        Timber.i("RegionOfRect: $cornerBounds")

        when (cornerBounds) {
            CornerBounds.Outside -> {
                rotate(
                    10F,
                    transformedDrawRectF.centerX(),
                    transformedDrawRectF.centerY()
                )
            }
            else -> {
            }
        }
        // Update inverted matrix
        updateInvertedDrawMatrix()
        drawMatrix.mapRect(transformedDrawRectF, drawRectF)
        drawMatrix.mapPoints(transformedDrawPoints, drawPoints)
        drawMatrix.mapRect(transformedLeftTopCornerBounds, leftTopCornerBounds)
        drawMatrix.mapRect(transformedRightTopCornerBounds, rightTopCornerBounds)
        drawMatrix.mapRect(transformedRightBottomCornerBounds, rightBottomCornerBounds)
        drawMatrix.mapRect(transformedLeftBottomCornerBounds, leftBottomCornerBounds)
    }

    fun actionMove(points: FloatArray, oldPoints: FloatArray): Boolean {
        if (cornerBounds == CornerBounds.Outside) return false

        val pointsForMapping = floatArrayOf(*points, *oldPoints)
        invertedDrawMatrix.mapPoints(pointsForMapping)

        val dScaleX = pointsForMapping[0] - pointsForMapping[2]
        val dScaleY = pointsForMapping[1] - pointsForMapping[3]

        when (cornerBounds) {
            CornerBounds.Center ->
                translate(points[0] - oldPoints[0], points[1] - oldPoints[1])
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

        drawMatrix.mapPoints(transformedDrawPoints, drawPoints)
        drawMatrix.mapRect(transformedLeftTopCornerBounds, leftTopCornerBounds)
        drawMatrix.mapRect(transformedRightTopCornerBounds, rightTopCornerBounds)
        drawMatrix.mapRect(transformedRightBottomCornerBounds, rightBottomCornerBounds)
        drawMatrix.mapRect(transformedLeftBottomCornerBounds, leftBottomCornerBounds)
        updateInvertedDrawMatrix()
        return true
    }

    fun onDraw(canvas: Canvas, paintTools: SnapPaintTools) {
        canvas.drawBitmap(mark.image, drawMatrix, null)

        canvas.drawLines(transformedDrawPoints, paintTools.borderPaint)

        canvas.drawRect(transformedLeftTopCornerBounds, paintTools.anchorPaint)
        canvas.drawRect(transformedRightTopCornerBounds, paintTools.anchorPaint)
        canvas.drawRect(transformedRightBottomCornerBounds, paintTools.anchorPaint)
        canvas.drawRect(transformedLeftBottomCornerBounds, paintTools.anchorPaint)
    }

    private fun getCornerBoundsByPoints(sourcePoints: FloatArray, points: FloatArray): CornerBounds {
        val x = points[0]
        val y = points[1]

        with(sourcePoints) {
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
            val centerBounds = RectF(sourcePoints[0], sourcePoints[1], sourcePoints[8], sourcePoints[9])

            return when {
                leftTopCornerBounds.contains(x, y) -> CornerBounds.LeftTop
                leftBottomCornerBounds.contains(x, y) -> CornerBounds.LeftBottom
                rightTopCornerBounds.contains(x, y) -> CornerBounds.RightTop
                rightBottomCornerBounds.contains(x, y) -> CornerBounds.RightBottom
                centerBounds.contains(x, y) -> CornerBounds.Center
                else -> CornerBounds.Outside
            }
        }
    }

    companion object {
        const val INBOUND = 30F
    }

}