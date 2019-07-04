package com.haejung.snapmark.presentation.snap.snapedit

import android.graphics.Bitmap
import android.net.Uri

interface BitmapImageProvider {

    fun getBitmap(uri: Uri): Bitmap

}