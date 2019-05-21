package com.haejung.snapmark.data

import android.graphics.Matrix
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class containing a preset
 */
@Entity(
    tableName = "mark_preset",
    foreignKeys = [ForeignKey(
        entity = Mark::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("mark_id")
    )]
)
data class MarkPreset(
    @ColumnInfo(name = "matrix", typeAffinity = ColumnInfo.BLOB) val matrix: Matrix,
    @ColumnInfo(name = "mark_id", index = true) val markId: Int? = null,
    @ColumnInfo(name = "name") val name: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preset_id")
    var presetId: Int = 0
}
