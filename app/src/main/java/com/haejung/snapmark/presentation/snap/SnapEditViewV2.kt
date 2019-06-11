package com.haejung.snapmark.presentation.snap

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import timber.log.Timber
import java.nio.FloatBuffer

class SnapEditViewV2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val translateMatrix = Matrix()
    private val scaleMatrix = Matrix()
    private val rotateMatrix = Matrix()

    private val drawRectF = RectF()
    private val drawPoints = FloatArray(8)

    private val transformedRect = RectF()
    private val scaleTransformedRect = RectF()
    private val transformedPoints = FloatArray(8)

    private val previousPoint = FloatArray(2) // Just containing x, y
    private val scaleAnchorPoint = PointF()

    private lateinit var cornerBounds: CornerBounds

    private var needToRender = false

    private val aColor = ContextCompat.getColor(context, R.color.textColorPrimary)
    private val bColor = ContextCompat.getColor(context, android.R.color.transparent)
    private val cColor = ContextCompat.getColor(context, android.R.color.holo_red_dark)

    private val borderPaint =
        Paint().apply {
            color = aColor
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

    private val realPointPaint =
        Paint().apply {
            color = aColor
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
            isAntiAlias = false
        }

    private val transformedPointPaint =
        Paint().apply {
            color = cColor
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
        }

    var mark: Mark? = null
        set(value) {
            field = value
            value?.image?.let {
                drawRectF.set(0F, 0F, it.width.toFloat(), it.height.toFloat())

                transformedRect.set(drawRectF)
                scaleTransformedRect.set(drawRectF)

                // FIXME: for debugging
                previousAnchorPoint.set(scaleTransformedRect.centerX(), scaleTransformedRect.centerY())

                drawPoints[0] = transformedRect.left
                drawPoints[1] = transformedRect.top
                drawPoints[2] = transformedRect.right
                drawPoints[3] = transformedRect.top
                drawPoints[4] = transformedRect.right
                drawPoints[5] = transformedRect.bottom
                drawPoints[6] = transformedRect.left
                drawPoints[7] = transformedRect.bottom

                drawPoints.forEachIndexed { index, value -> transformedPoints[index] = value }
                Timber.i("Image Size: ${it.width}, ${it.height}")

                translateMatrix.setTranslate(
                    width.toFloat() / 2F - it.width / 2F,
                    height.toFloat() / 2F - it.height / 2F
                )
                updateTransformMatrix(false)
            }
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawColor(bColor)
            it.drawLine(width / 2F, 0F, width / 2F, height.toFloat(), borderPaint)

            mark?.image?.let { bitmap ->
                it.drawBitmap(bitmap, transformer, null)
            }

            // FIXME: for debugging
            it.drawPoints(realPointBuffer.array(), realPointPaint)
            it.drawPoints(transformedPointBuffer.array(), transformedPointPaint)
        }
    }

    // FIXME: for debugging
    private val realPointBuffer = FloatBuffer.allocate(1000)
    private val transformedPointBuffer = FloatBuffer.allocate(1000)
    private val transformer = Matrix()
    private val invertedTransformerForTouching = Matrix()
    private val previousAnchorPoint = PointF()
    private var rotateAngle = 0F

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)

                    // A delta for translation
                    val dx = event.x - previousPoint[0]
                    val dy = event.y - previousPoint[1]

                    // For scale
                    val pointsForMapping = floatArrayOf(event.x, event.y, *previousPoint)
                    invertedTransformerForTouching.mapPoints(pointsForMapping)

                    val dScaleX = pointsForMapping[0] - pointsForMapping[2]
                    val dScaleY = pointsForMapping[1] - pointsForMapping[3]

                    Timber.i("dx:$dx [sx:$dScaleX], dy:$dy [sy:$dScaleY]")

                    needToRender = true
                    when (cornerBounds) {
                        CornerBounds.Center -> translateMatrix.postTranslate(dx, dy)
                        CornerBounds.RightBottom -> scaleMatrix.postScale(
                            1F + (1F * (dScaleX / scaleTransformedRect.width())),
                            1F + (1F * (dScaleY / scaleTransformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.RightTop -> scaleMatrix.postScale(
                            1F + (1F * (dScaleX / scaleTransformedRect.width())),
                            1F - (1F * (dScaleY / scaleTransformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.LeftBottom -> scaleMatrix.postScale(
                            1F - (1F * (dScaleX / scaleTransformedRect.width())),
                            1F + (1F * (dScaleY / scaleTransformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.LeftTop -> scaleMatrix.postScale(
                            1F - (1F * (dScaleX / scaleTransformedRect.width())),
                            1F - (1F * (dScaleY / scaleTransformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        else -> needToRender = false
                    }

                    if (needToRender) {

                        scaleMatrix.mapRect(scaleTransformedRect, drawRectF)
                        /*
                        transformer.mapRect(transformedRect, drawRectF)
                        transformer.mapPoints(transformedPoints, drawPoints)
                        */

                        previousPoint[0] = event.x
                        previousPoint[1] = event.y

                        updateTransformMatrix(false)
                        invalidate()
                    }
                }

                MotionEvent.ACTION_DOWN -> {

                    previousPoint[0] = event.x
                    previousPoint[1] = event.y

                    // For Debug
                    if (realPointBuffer.hasRemaining())
                        realPointBuffer.put(previousPoint)

                    // Update matrix
                    updateTransformMatrix(true)

                    // Transformed
                    val transformedPoint = FloatArray(2)
                    Matrix().apply { transformer.invert(this) }.mapPoints(transformedPoint, previousPoint)

                    // For Debug
                    if (transformedPointBuffer.hasRemaining())
                        transformedPointBuffer.put(transformedPoint)

                    scaleMatrix.mapRect(scaleTransformedRect, drawRectF)
                    /*
                    transformer.mapRect(transformedRect, drawRectF)
                    transformer.mapPoints(transformedPoints, drawPoints)
                    */

                    // Calculate corner of bounds
                    cornerBounds = getCornerBoundsByPoints(drawPoints, transformedPoint[0], transformedPoint[1])
                    Timber.i("RegionOfRect: $cornerBounds")

                    when (cornerBounds) {
                        CornerBounds.RightBottom -> scaleAnchorPoint.set(
                            scaleTransformedRect.left,
                            scaleTransformedRect.top
                        )
                        CornerBounds.RightTop -> scaleAnchorPoint.set(
                            scaleTransformedRect.left,
                            scaleTransformedRect.bottom
                        )
                        CornerBounds.LeftBottom -> scaleAnchorPoint.set(
                            scaleTransformedRect.right,
                            scaleTransformedRect.top
                        )
                        CornerBounds.LeftTop -> scaleAnchorPoint.set(
                            scaleTransformedRect.right,
                            scaleTransformedRect.bottom
                        )
                        CornerBounds.Outside -> {
                            // FIXME: Test type 2
                            val diffPoint = floatArrayOf(
                                previousAnchorPoint.x - scaleTransformedRect.centerX(),
                                previousAnchorPoint.y - scaleTransformedRect.centerY()
                            )
                            Timber.i("DiffPoint: ${diffPoint[0]}, ${diffPoint[1]}")

                            Matrix().apply {
                                setRotate(rotateAngle)
                            }.mapPoints(diffPoint)

                            Timber.i("Mapped DiffPoint: ${diffPoint[0]}, ${diffPoint[1]}")

                            // Rotate
                            rotateAngle += 45
                            rotateMatrix.setRotate(
                                rotateAngle,
                                scaleTransformedRect.centerX(),
                                scaleTransformedRect.centerY()
                            )

                            // Translate
                            rotateMatrix.postTranslate(diffPoint[1], diffPoint[0])

                            previousAnchorPoint.set(scaleTransformedRect.centerX(), scaleTransformedRect.centerY())
                            Timber.i("scaleTransformedRect X: ${scaleTransformedRect.centerX()}, ${scaleTransformedRect.centerY()}")
                            Timber.i("rotateMatrix: ${rotateMatrix.toShortString()}")
                        }
                        else -> {
                        }
                    }
                    // Update matrix
                    updateTransformMatrix(true)
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

    private fun updateTransformMatrix(withInvertedMatrix: Boolean) {
        with(transformer) {
            reset()
            postConcat(scaleMatrix)
            postConcat(rotateMatrix)
            postConcat(translateMatrix)
        }

        if (withInvertedMatrix) {
            invertedTransformerForTouching.apply {
                reset()
                Matrix().also {
                    it.postConcat(rotateMatrix)
                }.invert(this)
            }
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
                this[2] - INBOUND,
                this[3] - INBOUND,
                this[2] + INBOUND,
                this[3] + INBOUND
            )
            val rightBottomCornerBounds = RectF(
                this[4] - INBOUND,
                this[5] - INBOUND,
                this[4] + INBOUND,
                this[5] + INBOUND
            )
            val leftBottomCornerBounds = RectF(
                this[6] - INBOUND,
                this[7] - INBOUND,
                this[6] + INBOUND,
                this[7] + INBOUND
            )
            val centerBounds = RectF(sourcePoints[0], sourcePoints[1], sourcePoints[4], sourcePoints[5])

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
        const val INBOUND = 20F
    }

}