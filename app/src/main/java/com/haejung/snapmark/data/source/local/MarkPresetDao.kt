package com.haejung.snapmark.data.source.local

import androidx.room.*
import com.haejung.snapmark.data.MarkPreset
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MarkPresetDao {

    @Query("SELECT * FROM mark_preset")
    fun getAll(): Flowable<List<MarkPreset>>

    @Query("SELECT * FROM mark_preset WHERE preset_id = :presetId")
    fun getById(presetId: Int): Flowable<MarkPreset>

    @Query("SELECT * FROM mark_preset WHERE name = :name")
    fun getByName(name: String): Flowable<MarkPreset>

    @Query("SELECT * FROM mark_preset WHERE mark_id = :markId")
    fun getAllByMarkId(markId: Int): Flowable<List<MarkPreset>>

    @Query("SELECT mark_preset.preset_id, mark_preset.matrix, mark_preset.mark_id, mark_preset.name FROM mark_preset INNER JOIN mark on mark.id = mark_id WHERE mark.name = :markName")
    fun getAllByMarkName(markName: String): Flowable<List<MarkPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg markPreset: MarkPreset): Completable

    @Update
    fun update(markPreset: MarkPreset): Completable

    @Delete
    fun delete(markPreset: MarkPreset): Completable

    @Query("DELETE FROM mark_preset WHERE preset_id = :presetId")
    fun deleteById(presetId: Int): Completable

    @Query("DELETE FROM mark_preset WHERE name = :name")
    fun deleteByName(name: String): Completable

    @Query("DELETE FROM mark_preset")
    fun deleteAll(): Single<Int>

}