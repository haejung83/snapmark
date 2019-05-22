package com.haejung.snapmark.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class containing a mark
 */
@Entity(tableName = "mark")
data class Mark(
    @ColumnInfo(name = "thumbnail", typeAffinity = ColumnInfo.BLOB) val thumbnail: Bitmap,
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB) val image: Bitmap,
    @ColumnInfo(name = "name") val name: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
