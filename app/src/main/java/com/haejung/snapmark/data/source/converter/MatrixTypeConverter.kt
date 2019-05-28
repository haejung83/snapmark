package com.haejung.snapmark.data.source.converter

import android.graphics.Matrix
import androidx.room.TypeConverter
import com.haejung.snapmark.extend.toByteArray
import com.haejung.snapmark.extend.toFloatArray

class MatrixTypeConverter {

    @TypeConverter
    fun fromMatrix(matrix: Matrix?): ByteArray? =
        matrix?.let {
            val outByteArray = FloatArray(MATRIX_INNER_FLOAT_SIZE)
            it.getValues(outByteArray)
            outByteArray.toByteArray()
        }

    @TypeConverter
    fun toMatrix(matrixByteArray: ByteArray?): Matrix? =
        matrixByteArray?.let {
            val matrix = Matrix()
            matrix.setValues(it.toFloatArray())
            matrix
        }

    companion object {
        private const val MATRIX_INNER_FLOAT_SIZE = 9
    }

}