package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.haejung.snapmark.data.Mark
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintTools = SnapPaintTools(context)
    private var oldPoints = floatArrayOf()
    private var snapEditImage: SnapEditImage? = null
    private var snapAutoScaleImage: SnapAutoScaleImage? = null

    private val windowRectF: RectF
        get() = RectF(0F, 0F, width.toFloat(), height.toFloat())

    var mark: Mark? = null
        set(value) {
            field = value
            value?.let { snapEditImage = SnapEditImage(it.image, windowRectF) }
        }
    var targetImage: Bitmap? = null
        set(value) {
            field = value
            value?.let { snapAutoScaleImage = SnapAutoScaleImage(it, windowRectF) }
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            snapAutoScaleImage?.onDraw(it, paintTools)
            snapEditImage?.onDraw(it, paintTools)
        }
    }

    private fun drawForExtract(canvas: Canvas) {
        snapAutoScaleImage?.onDraw(canvas)
        snapEditImage?.onDraw(canvas)
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

    private fun extractBitmap(): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            drawForExtract(Canvas(this))
        }

    fun save(path: String) {
        Timber.i("Save: $path")
        File(path).let {
            if (it.exists()) {
                it.delete()
            }

            Completable.fromCallable {
                val extractedBitmap = extractBitmap()
                FileOutputStream(it).apply {
                    extractedBitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
                    flush()
                }.close()
                extractedBitmap.recycle()
            }
                .subscribeOn(Schedulers.io())
                .doOnComplete { Timber.i("Saved image") }
                .subscribe()
        }
    }

}