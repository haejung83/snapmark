package com.haejung.snapmark.presentation.snap.snapedit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.presentation.snap.Snap

class SnapEditExtractor(
    private val bitmapImageProvider: BitmapImageProvider,
    private val windowRectF: RectF
) {

    private var snapEditImage: SnapEditImage? = null
    private var snapAutoScaleImage: SnapAutoScaleImage? = null

    var mark: Mark? = null
        set(value) {
            field = value
            value?.let {
                snapEditImage?.dispose()
                snapEditImage = SnapEditImage(it.image, windowRectF)
            }
        }

    private fun drawForExtract(canvas: Canvas) {
        snapAutoScaleImage?.onDraw(canvas)
        snapEditImage?.onDraw(canvas)
    }

    fun extractBitmap(snap: Snap): Bitmap {
        snap.let {
            snapEditImage?.snapMatrix = it.snapMatrix
            snapAutoScaleImage?.dispose()
            snapAutoScaleImage =
                SnapAutoScaleImage(
                    bitmapImageProvider.getBitmap(it.targetImage), windowRectF
                )
        }
        return Bitmap.createBitmap(windowRectF.width().toInt(), windowRectF.height().toInt(), Bitmap.Config.ARGB_8888)
            .run {
                drawForExtract(Canvas(this))
                snapAutoScaleImage?.let {
                    val cropRectF = it.autoScaledRectF
                    val croppedBitmap = Bitmap.createBitmap(
                        this,
                        cropRectF.left.toInt(),
                        cropRectF.top.toInt(),
                        cropRectF.width().toInt(),
                        cropRectF.height().toInt()
                    )
                    this.recycle()
                    croppedBitmap
                } ?: this
            }
    }

}