package com.haejung.snapmark.presentation.snap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.haejung.snapmark.R
import timber.log.Timber

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val drawRectF = RectF()
    private val aColor: Int by lazy {
        context.getColor(R.color.textColorPrimary)
    }
    private val bColor: Int by lazy {
        context.getColor(R.color.colorPrimaryDark)
    }
    private val borderPaint: Paint by lazy {
        Paint().apply {
            color = aColor
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Timber.i("onDraw")

        canvas?.let {
//            it.drawColor(bColor)
            val copyRectF = drawRectF
            copyRectF.inset( STROKE_WIDTH / 2F, STROKE_WIDTH / 2F)
            it.drawRect(copyRectF, borderPaint)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Timber.i("onLayout $changed, $left, $top, $right, $bottom")
        drawRectF.set(INBOUND, INBOUND, width - INBOUND, height - INBOUND)
    }

    companion object {
        const val STROKE_WIDTH = 4F
        const val INBOUND = 50F
    }

}