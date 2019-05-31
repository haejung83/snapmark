package com.haejung.snapmark.presentation.snap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
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
    private var previousCoordinate = FloatArray(2)
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
        canvas?.let {
            it.drawColor(bColor)
            mark?.image?.let { bitmap ->
                it.drawBitmap(bitmap, drawMatrix, null)
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
//            it.drawRect(transformedRect, borderPaint)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Timber.i("onLayout $changed, $left, $top, $right, $bottom")
        drawRectF.set(INBOUND, INBOUND, width - INBOUND, height - INBOUND)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    val dx = event.x - previousCoordinate[0]
                    val dy = event.y - previousCoordinate[1]

                    when (cornerBounds) {
                        CornerBounds.CENTER -> drawMatrix.postTranslate(dx, dy)
                        CornerBounds.RIGHT_BOTTOM -> drawMatrix.postScale(
                            transformedRect.width() / (transformedRect.width() - dx * 2),
                            transformedRect.height() / (transformedRect.height() - dy * 2),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.RIGHT_TOP -> drawMatrix.postScale(
                            transformedRect.width() / (transformedRect.width() - dx * 2),
                            transformedRect.height() / (transformedRect.height() + dy * 2),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.LEFT_BOTTOM -> drawMatrix.postScale(
                            transformedRect.width() / (transformedRect.width() + dx * 2),
                            transformedRect.height() / (transformedRect.height() - dy * 2),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                        CornerBounds.LEFT_TOP -> drawMatrix.postScale(
                            transformedRect.width() / (transformedRect.width() + dx * 2),
                            transformedRect.height() / (transformedRect.height() + dy * 2),
                            transformedRect.centerX(),
                            transformedRect.centerY()
                        )
                    }

                    drawMatrix.mapRect(transformedRect, drawRectF)
                    drawMatrix.mapPoints(transformedPoints, drawPoints)
                    previousCoordinate[0] = event.x
                    previousCoordinate[1] = event.y
                    invalidate()
                }
                MotionEvent.ACTION_DOWN -> {
                    previousCoordinate[0] = event.x
                    previousCoordinate[1] = event.y
                    cornerBounds = getRegionOfRect(transformedRect, event.x, event.y)
                    Timber.i("RegionOfRect: $cornerBounds")
                    // FIXME: for testing
                    drawMatrix.postRotate(5f)
                }
                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                else -> {
                    return false
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    enum class CornerBounds {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM, CENTER, OUTSIDE
    }

    private fun getRegionOfRect(sourceRect: RectF, x: Float, y: Float): CornerBounds {
        with(sourceRect) {
            val leftTopCornerBounds = RectF(left - INBOUND, top - INBOUND, left + INBOUND, top + INBOUND)
            val leftBottomCornerBounds = RectF(left - INBOUND, bottom - INBOUND, left + INBOUND, bottom + INBOUND)
            val rightTopCornerBounds = RectF(right - INBOUND, top - INBOUND, right + INBOUND, top + INBOUND)
            val rightBottomCornerBounds = RectF(right - INBOUND, bottom - INBOUND, right + INBOUND, bottom + INBOUND)

            return when {
                leftTopCornerBounds.contains(x, y) -> CornerBounds.LEFT_TOP
                leftBottomCornerBounds.contains(x, y) -> CornerBounds.LEFT_BOTTOM
                rightTopCornerBounds.contains(x, y) -> CornerBounds.RIGHT_TOP
                rightBottomCornerBounds.contains(x, y) -> CornerBounds.RIGHT_BOTTOM
                sourceRect.contains(x, y) -> CornerBounds.CENTER
                else -> CornerBounds.OUTSIDE
            }
        }
    }

    companion object {
        const val STROKE_WIDTH = 4F
        const val INBOUND = 50F
    }

}