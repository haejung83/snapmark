package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import timber.log.Timber

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val drawMatrix = Matrix()
    private val invertedDrawMatrix = Matrix()

    private val drawRectF = RectF()
    private val transformedRect = RectF()

    private val drawPoints = FloatArray(16)
    private val transformedPoints = FloatArray(16)

    private val previousTouchPoints = FloatArray(2)

    private lateinit var cornerBounds: CornerBounds

    private var needToRender = false

    private val borderPaint =
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

    var mark: Mark? = null
        set(value) {
            field = value
            value?.image?.let {
                Timber.i("Image Size: ${it.width}, ${it.height}")

                drawRectF.set(0F, 0F, it.width.toFloat(), it.height.toFloat())
                transformedRect.set(drawRectF)

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
                drawPoints.copyInto(transformedPoints)

                translate(
                    width.toFloat() / 2F - it.width / 2F,
                    height.toFloat() / 2F - it.height / 2F
                )

                updateTransformMatrix()
                drawMatrix.mapPoints(transformedPoints, drawPoints)
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            mark?.image?.let { bitmap ->
                it.drawBitmap(bitmap, drawMatrix, null)
                it.drawLines(transformedPoints, borderPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)

                    // A delta for translation
                    val dx = event.x - previousTouchPoints[0]
                    val dy = event.y - previousTouchPoints[1]

                    val pointsForMapping = floatArrayOf(event.x, event.y, *previousTouchPoints)
                    invertedDrawMatrix.mapPoints(pointsForMapping)

                    val dScaleX = pointsForMapping[0] - pointsForMapping[2]
                    val dScaleY = pointsForMapping[1] - pointsForMapping[3]

                    Timber.i("dx:$dx [sx:$dScaleX], dy:$dy [sy:$dScaleY]")

                    needToRender = true
                    when (cornerBounds) {
                        CornerBounds.Center -> translate(dx, dy)
                        CornerBounds.RightBottom -> scale(
                            1F + (2F * (dScaleX / drawRectF.width())),
                            1F + (2F * (dScaleY / drawRectF.height())),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.RightTop -> scale(
                            1F + (2F * (dScaleX / drawRectF.width())),
                            1F - (2F * (dScaleY / drawRectF.height())),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.LeftBottom -> scale(
                            1F - (2F * (dScaleX / drawRectF.width())),
                            1F + (2F * (dScaleY / drawRectF.height())),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.LeftTop -> scale(
                            1F - (2F * (dScaleX / drawRectF.width())),
                            1F - (2F * (dScaleY / drawRectF.height())),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        else -> needToRender = false
                    }

                    if (needToRender) {
//                        drawMatrix.mapRect(transformedRect, drawRectF)
                        drawMatrix.mapPoints(transformedPoints, drawPoints)

                        previousTouchPoints[0] = event.x
                        previousTouchPoints[1] = event.y

                        updateTransformMatrix()
                        invalidate()
                    }
                }

                MotionEvent.ACTION_DOWN -> {

                    previousTouchPoints[0] = event.x
                    previousTouchPoints[1] = event.y

                    // Update matrix
                    updateTransformMatrix()
                    drawMatrix.mapRect(transformedRect, drawRectF)

                    // Transformed
                    val transformedPoint = FloatArray(2)
                    Matrix().apply { drawMatrix.invert(this) }.mapPoints(transformedPoint, previousTouchPoints)

                    // Calculate corner of bounds
                    cornerBounds = getCornerBoundsByPoints(drawPoints, transformedPoint[0], transformedPoint[1])
                    Timber.i("RegionOfRect: $cornerBounds")

                    when (cornerBounds) {
                        CornerBounds.Outside -> {
                            rotate(
                                10F,
                                transformedRect.centerX(),
                                transformedRect.centerY()
                            )
                        }
                        else -> {
                        }
                    }
                    // Update matrix
                    updateTransformMatrix()
                    drawMatrix.mapRect(transformedRect, drawRectF)
                    drawMatrix.mapPoints(transformedPoints, drawPoints)

                    // Request to draw
                    invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                else -> return false
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun updateTransformMatrix() {
        invertedDrawMatrix.apply {
            drawMatrix.invert(this)
        }
    }

    sealed class CornerBounds {
        object LeftTop : CornerBounds()
        object LeftBottom : CornerBounds()
        object RightTop : CornerBounds()
        object RightBottom : CornerBounds()
        object Center : CornerBounds()
        object Outside : CornerBounds()
    }

    private fun getCornerBoundsByPoints(sourcePoints: FloatArray, x: Float, y: Float): CornerBounds {
        with(sourcePoints) {
            val leftTopCornerBounds = RectF(
                this[0] - INBOUND,
                this[1] - INBOUND,
                this[0] + INBOUND,
                this[1] + INBOUND
            )
            val rightTopCornerBounds = RectF(
                this[4] - INBOUND,
                this[5] - INBOUND,
                this[4] + INBOUND,
                this[5] + INBOUND
            )
            val rightBottomCornerBounds = RectF(
                this[8] - INBOUND,
                this[9] - INBOUND,
                this[8] + INBOUND,
                this[9] + INBOUND
            )
            val leftBottomCornerBounds = RectF(
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
        const val STROKE_WIDTH = 4F
        const val INBOUND = 30F
    }

}