package com.haejung.snapmark.presentation.snap

import android.graphics.Bitmap
import com.haejung.snapmark.data.Mark

data class Snap(
    val mark: Mark,
    val image: Bitmap
)