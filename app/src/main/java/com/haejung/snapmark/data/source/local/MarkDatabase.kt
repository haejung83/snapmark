package com.haejung.snapmark.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.MarkPreset
import com.haejung.snapmark.data.source.converter.BitmapTypeConverter
import com.haejung.snapmark.data.source.converter.MatrixTypeConverter

@Database(
    entities = [
        Mark::class,
        MarkPreset::class],
    exportSchema = false, version = 1
)
@TypeConverters(
    BitmapTypeConverter::class,
    MatrixTypeConverter::class
)
abstract class MarkDatabase : RoomDatabase() {

    abstract fun markDao(): MarkDao
    abstract fun markPresetDao(): MarkPresetDao

    companion object {
        private var instance: MarkDatabase? = null

        fun getInstance(context: Context): MarkDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MarkDatabase::class.java,
                    "SnapMark.db"
                ).build().apply {
                    instance = this
                }
            }
        }
    }

}