package com.haejung.snapmark.data.source.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapTypeConverter {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? =
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        }

    @TypeConverter
    fun toBitmap(bitmapByteArray: ByteArray?): Bitmap? =
        bitmapByteArray?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }

}