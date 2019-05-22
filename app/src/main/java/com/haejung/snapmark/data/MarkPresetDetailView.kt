package com.haejung.snapmark.data

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    "SELECT mark_preset.preset_id, mark_preset.name, mark_preset.matrix, mark.image FROM mark_preset INNER JOIN mark ON mark.id = mark_id"
)
data class MarkPresetDetailView(
    @ColumnInfo(name = "preset_id")
    val presetId: Int,
    val name: String,
    val matrix: Matrix,
    val image: Bitmap
)
