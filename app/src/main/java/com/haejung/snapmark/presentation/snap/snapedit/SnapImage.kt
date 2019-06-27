package com.haejung.snapmark.presentation.snap.snapedit

import android.graphics.Bitmap
import android.graphics.RectF

abstract class SnapImage(protected val image: Bitmap, protected val window: RectF) {

    fun dispose() {
        if (!image.isRecycled)
            image.recycle()
    }

}