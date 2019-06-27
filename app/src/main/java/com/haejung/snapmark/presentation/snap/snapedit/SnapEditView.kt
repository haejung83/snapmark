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

    var snap: Snap? = null
        set(value) {
            field = value
            value?.let {
                snapEditImage?.drawMatrix = it.markMatrix
                snapAutoScaleImage?.dispose()
                snapAutoScaleImage =
                    SnapAutoScaleImage(
                        getBitmapFromUri(it.targetImage), windowRectF
                    )
            }
        }

    var mark: Mark? = null
        set(value) {
            field = value
            value?.let {
                snapEditImage?.dispose()
                snapEditImage = SnapEditImage(it.image, windowRectF)
            }
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

    fun save(targetSavePath: String) {
        Timber.i("Save: $targetSavePath")
        File(targetSavePath).let {
            if (it.exists()) it.delete()
            val extractedBitmap = extractBitmap()
            FileOutputStream(it).apply {
                extractedBitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
                flush()
            }.close()
            extractedBitmap.recycle()
        }
    }

}