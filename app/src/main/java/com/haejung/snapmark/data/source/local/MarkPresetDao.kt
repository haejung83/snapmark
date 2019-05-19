package com.haejung.snapmark.data.source.local

import androidx.room.*
import com.haejung.snapmark.data.MarkPreset

@Dao
interface MarkPresetDao {

    @Query("SELECT * FROM MarkPreset")
    fun getAll(): List<MarkPreset>

    @Query("SELECT * FROM MarkPreset WHERE preset_id = :presetId")
    fun getById(presetId: Int): MarkPreset

    @Query("SELECT * FROM MarkPreset WHERE name = :name")
    fun getByName(name: String): MarkPreset

    @Query("SELECT * FROM MarkPreset WHERE mark_id = :markId")
    fun getAllByMarkId(markId: Int): List<MarkPreset>

    @Query("SELECT * FROM MarkPreset INNER JOIN mark on mark.id = mark_id WHERE mark.name = :markName")
    fun getAllByMarkName(markName: String): List<MarkPreset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg markPreset: MarkPreset)

    @Update
    fun update(markPreset: MarkPreset)

    @Delete
    fun delete(markPreset: MarkPreset)

    @Query("DELETE FROM MarkPreset WHERE preset_id = :presetId")
    fun deleteById(presetId: Int)

    @Query("DELETE FROM MarkPreset WHERE name = :name")
    fun deleteByName(name: String)

    @Query("DELETE FROM MarkPreset")
    fun deleteAll()

}