package com.haejung.snapmark.data

import androidx.room.*

/**
 * Data class containing a preset
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Mark::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("mark_id")
    )]
)
data class MarkPreset(
    @Embedded val matrix: MarkMatrix,
    @ColumnInfo(name = "mark_id", index = true) val markId: Int? = null,
    @ColumnInfo(name = "name") val name: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preset_id")
    var presetId: Int = 0
}

/**
 * Data class containing a presentation of Mark in preset
 *
 * A position relative to applied target image
 * A scale of mark
 * A rotation of mark
 */
data class MarkMatrix(
    @ColumnInfo(name = "position", typeAffinity = ColumnInfo.BLOB) var position: ByteArray,
    @ColumnInfo(name = "scale", typeAffinity = ColumnInfo.BLOB) var scale: ByteArray,
    @ColumnInfo(name = "rotation", typeAffinity = ColumnInfo.BLOB) var rotation: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MarkMatrix

        if (!position.contentEquals(other.position)) return false
        if (!scale.contentEquals(other.scale)) return false
        if (!rotation.contentEquals(other.rotation)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.contentHashCode()
        result = 31 * result + scale.contentHashCode()
        result = 31 * result + rotation.contentHashCode()
        return result
    }

}
