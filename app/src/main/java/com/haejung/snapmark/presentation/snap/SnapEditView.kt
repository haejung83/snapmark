package com.haejung.snapmark.presentation.snap

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import timber.log.Timber

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val drawMatrix = Matrix()
    private val drawRectF = RectF()
    private val drawPoints = FloatArray(8)
    private val transformedRect = RectF()
    private val transformedPoints = FloatArray(8)
    private val previousPoint = PointF()
    private val scaleAnchorPoint = PointF()
    private var cornerBounds = CornerBounds.CENTER

    private val aColor: Int by lazy {
        context.getColor(R.color.textColorPrimary)
    }
    private val bColor: Int by lazy {
        context.getColor(android.R.color.transparent)
    }
    private val borderPaint: Paint by lazy {
        Paint().apply {
            color = aColor
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    var mark: Mark? = null
        set(value) {
            field = value
            value?.image?.let {
                drawRectF.set(0F, 0F, it.width.toFloat(), it.height.toFloat())
                transformedRect.set(drawRectF)
                drawPoints[0] = transformedRect.left
                drawPoints[1] = transformedRect.top
                drawPoints[2] = transformedRect.right
                drawPoints[3] = transformedRect.top
                drawPoints[4] = transformedRect.right
                drawPoints[5] = transformedRect.bottom
                drawPoints[6] = transformedRect.left
                drawPoints[7] = transformedRect.bottom
                drawPoints.forEachIndexed { index, value -> transformedPoints[index] = value }
            }
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        Timber.i("onDraw")
        canvas?.let {
            it.drawColor(bColor)
            it.drawLine(width / 2F, 0F, width / 2F, height.toFloat(), borderPaint)

            mark?.image?.let { bitmap ->
                it.drawBitmap(bitmap, drawMatrix, null)
                it.drawRect(transformedRect, borderPaint)
                it.drawLine(
                    transformedPoints[0],
                    transformedPoints[1],
                    transformedPoints[2],
                    transformedPoints[3],
                    borderPaint
                )
                it.drawLine(
                    transformedPoints[2],
                    transformedPoints[3],
                    transformedPoints[4],
                    transformedPoints[5],
                    borderPaint
                )
                it.drawLine(
                    transformedPoints[4],
                    transformedPoints[5],
                    transformedPoints[6],
                    transformedPoints[7],
                    borderPaint
                )
                it.drawLine(
                    transformedPoints[6],
                    transformedPoints[7],
                    transformedPoints[0],
                    transformedPoints[1],
                    borderPaint
                )
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Timber.i("onLayout $changed, $left, $top, $right, $bottom")
        drawRectF.set(INBOUND, INBOUND, width - INBOUND, height - INBOUND)
    }

    private var needToRender = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    val dx = event.x - previousPoint.x
                    val dy = event.y - previousPoint.y

                    needToRender = true
                    when (cornerBounds) {
                        CornerBounds.CENTER -> drawMatrix.postTranslate(dx, dy)
                        CornerBounds.RIGHT_BOTTOM -> drawMatrix.postScale(
                            1F + (1F * (dx / transformedRect.width())),
                            1F + (1F * (dy / transformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.RIGHT_TOP -> drawMatrix.postScale(
                            1F + (1F * (dx / transformedRect.width())),
                            1F - (1F * (dy / transformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.LEFT_BOTTOM -> drawMatrix.postScale(
                            1F - (1F * (dx / transformedRect.width())),
                            1F + (1F * (dy / transformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        CornerBounds.LEFT_TOP -> drawMatrix.postScale(
                            1F - (1F * (dx / transformedRect.width())),
                            1F - (1F * (dy / transformedRect.height())),
                            scaleAnchorPoint.x,
                            scaleAnchorPoint.y
                        )
                        else -> needToRender = false
                    }

                    if (needToRender) {
                        drawMatrix.mapRect(transformedRect, drawRectF)
                        drawMatrix.mapPoints(transformedPoints, drawPoints)
                        previousPoint.set(event.x, event.y)
                        invalidate()
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    previousPoint.set(event.x, event.y)
                    cornerBounds = getCornerBoundsByPoints(transformedPoints, event.x, event.y)
                    Timber.i("RegionOfRect: $cornerBounds")
                    when (cornerBounds) {
                        CornerBounds.RIGHT_BOTTOM -> scaleAnchorPoint.set(transformedRect.left, transformedRect.top)
                        CornerBounds.RIGHT_TOP -> scaleAnchorPoint.set(transformedRect.left, transformedRect.bottom)
                        CornerBounds.LEFT_BOTTOM -> scaleAnchorPoint.set(transformedRect.right, transformedRect.top)
                        CornerBounds.LEFT_TOP -> scaleAnchorPoint.set(transformedRect.right, transformedRect.bottom)
                        else -> {
                        }
                    }
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

    enum class CornerBounds {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM, CENTER, OUTSIDE
    }

    private fun getCornerBoundsByPoints(sourcePoints: FloatArray, x: Float, y: Float): CornerBounds {
        with(sourcePoints) {
            val leftTopCornerBounds = RectF(sourcePoints[0] - INBOUND, sourcePoints[1] - INBOUND, sourcePoints[0] + INBOUND, sourcePoints[1] + INBOUND)
            val rightTopCornerBounds = RectF(sourcePoints[2] - INBOUND, sourcePoints[3] - INBOUND, sourcePoints[2] + INBOUND, sourcePoints[3] + INBOUND)
            val rightBottomCornerBounds = RectF(sourcePoints[4] - INBOUND, sourcePoints[5] - INBOUND, sourcePoints[4] + INBOUND, sourcePoints[5] + INBOUND)
            val leftBottomCornerBounds = RectF(sourcePoints[6] - INBOUND, sourcePoints[7] - INBOUND, sourcePoints[6] + INBOUND, sourcePoints[7] + INBOUND)
            val centerBounds = RectF(sourcePoints[0], sourcePoints[1], sourcePoints[4], sourcePoints[5])

            return when {
                leftTopCornerBounds.contains(x, y) -> CornerBounds.LEFT_TOP
                leftBottomCornerBounds.contains(x, y) -> CornerBounds.LEFT_BOTTOM
                rightTopCornerBounds.contains(x, y) -> CornerBounds.RIGHT_TOP
                rightBottomCornerBounds.contains(x, y) -> CornerBounds.RIGHT_BOTTOM
                centerBounds.contains(x, y) -> CornerBounds.CENTER
                else -> CornerBounds.OUTSIDE
            }
        }
    }

    companion object {
        const val STROKE_WIDTH = 4F
        const val INBOUND = 10F
    }

}