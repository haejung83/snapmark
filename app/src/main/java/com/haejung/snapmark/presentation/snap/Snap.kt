package com.haejung.snapmark.presentation.snap

import android.graphics.Matrix
import android.net.Uri

data class SnapMatrix(
    var matrix: Matrix = Matrix(),
    var rotateAngle: Double = 0.0
)

data class Snap(
    val targetImage: Uri,
    val snapMatrix: SnapMatrix = SnapMatrix()
)