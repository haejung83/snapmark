package com.haejung.snapmark.presentation.snap

import android.graphics.Matrix
import android.net.Uri

data class Snap(val targetImage: Uri, val markMatrix: Matrix = Matrix())