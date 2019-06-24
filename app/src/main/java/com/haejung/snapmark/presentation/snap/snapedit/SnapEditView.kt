package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.haejung.snapmark.data.Mark
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintTools = SnapPaintTools(context)
    private var snapEditImage: SnapEditImage? = null
    private var oldPoints = floatArrayOf()

    var mark: Mark? = null
        set(value) {
            field = value
            value?.let { snapEditImage = SnapEditImage(it.image, getWindowRectF()) }
        }
    var targetImage: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            targetImage?.let { target ->
                it.drawBitmap(
                    target,
                    null,
                    getWindowRectF(),
                    null
                )
            }
            snapEditImage?.onDraw(it, paintTools)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    val newPoints = floatArrayOf(event.x, event.y)
                    val needToRender = snapEditImage?.actionMove(newPoints, oldPoints) ?: false
                    newPoints.copyInto(oldPoints)
                    if (needToRender) invalidate()
                }
                MotionEvent.ACTION_DOWN -> {
                    oldPoints = floatArrayOf(event.x, event.y)
                    snapEditImage?.actionStart(oldPoints)
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

    fun extractBitmap() =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            draw(Canvas(this))
        }

    fun save(path: String) {
        Timber.i("Save: $path")
        File(path).let {
            if(it.exists()) {
                it.delete()
            }

            FileOutputStream(it).apply {
                extractBitmap().compress(Bitmap.CompressFormat.PNG, 100, this)
                flush()
            }.close()
        }
    }

    private fun getWindowRectF() =
        RectF(0F, 0F, width.toFloat(), height.toFloat())

}