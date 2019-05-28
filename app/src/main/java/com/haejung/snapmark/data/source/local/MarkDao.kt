package com.haejung.snapmark.data.source.local

import androidx.room.*
import com.haejung.snapmark.data.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MarkDao {

    @Query("SELECT * FROM mark")
    fun getAll(): Flowable<List<Mark>>

    @Query("SELECT * FROM mark WHERE id = :id")
    fun getById(id: Int): Flowable<Mark>

    @Query("SELECT * FROM mark WHERE name = :name")
    fun getByName(name: String): Flowable<Mark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg marks: Mark): Completable

    @Update
    fun update(mark: Mark): Completable

    @Delete
    fun delete(mark: Mark): Completable

    @Query("DELETE FROM mark WHERE id = :id")
    fun deleteById(id: Int): Completable

    @Query("DELETE FROM mark WHERE name = :name")
    fun deleteByName(name: String): Completable

    @Query("DELETE FROM mark")
    fun deleteAll(): Single<Int>

}