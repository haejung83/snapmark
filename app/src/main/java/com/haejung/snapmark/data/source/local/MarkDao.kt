package com.haejung.snapmark.data.source.local

import androidx.room.*
import com.haejung.snapmark.data.Mark

@Dao
interface MarkDao {

    @Query("SELECT * FROM mark")
    fun getAll(): List<Mark>

    @Query("SELECT * FROM mark WHERE id = :id")
    fun getById(id: Int): Mark

    @Query("SELECT * FROM mark WHERE name = :name")
    fun getByName(name: String): Mark

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg marks: Mark)

    @Update
    fun update(mark: Mark)

    @Delete
    fun delete(mark: Mark)

    @Query("DELETE FROM mark WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM mark WHERE name = :name")
    fun deleteByName(name: String)

    @Query("DELETE FROM mark")
    fun deleteAll()

}