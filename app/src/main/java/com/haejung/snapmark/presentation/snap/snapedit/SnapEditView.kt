package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.haejung.snapmark.data.Mark

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintTools = SnapPaintTools(context)
    private var snapEditMark: SnapEditMark? = null
    var mark: Mark? = null
        set(value) {
            field = value
            value?.let { snapEditMark = SnapEditMark(it, getWindowRectF()) }
        }
    private var oldPoints = floatArrayOf()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            snapEditMark?.onDraw(it, paintTools)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    val newPoints = floatArrayOf(event.x, event.y)
                    val needToRender = snapEditMark?.actionMove(newPoints, oldPoints) ?: false
                    newPoints.copyInto(oldPoints)
                    if (needToRender) invalidate()
                }
                MotionEvent.ACTION_DOWN -> {
                    oldPoints = floatArrayOf(event.x, event.y)
                    snapEditMark?.actionStart(oldPoints)
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

    private fun getWindowRectF() =
        RectF(0F, 0F, width.toFloat(), height.toFloat())

}