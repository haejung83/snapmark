package com.haejung.snapmark.presentation.snap.snapedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.presentation.snap.Snap

class SnapEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintTools = SnapPaintTools(context)
    private var oldPoints = floatArrayOf()
    private var snapEditImage: SnapEditImage? = null
    private var snapAutoScaleImage: SnapAutoScaleImage? = null

    private val windowRectF: RectF
        get() = RectF(0F, 0F, width.toFloat(), height.toFloat())

    private val bitmapImageProvider by lazy(LazyThreadSafetyMode.NONE) {
        object : BitmapImageProvider {
            override fun getBitmap(uri: Uri): Bitmap =
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    val extractor by lazy(LazyThreadSafetyMode.NONE) {
        SnapEditExtractor(bitmapImageProvider, windowRectF)
    }

    var snap: Snap? = null
        set(value) {
            field = value
            value?.let {
                snapEditImage?.snapMatrix = it.snapMatrix
                snapAutoScaleImage?.dispose()
                snapAutoScaleImage =
                    SnapAutoScaleImage(
                        getBitmapFromUri(it.targetImage), windowRectF
                    )
            }
            invalidate()
        }

    var mark: Mark? = null
        set(value) {
            field = value
            value?.let {
                snapEditImage?.dispose()
                snapEditImage = SnapEditImage(it.image, windowRectF)
            }
            invalidate()
        }

    private fun getBitmapFromUri(uri: Uri) =
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            snapAutoScaleImage?.onDraw(it, paintTools)
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

}