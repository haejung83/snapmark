package com.haejung.snapmark.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.haejung.snapmark.data.source.local.MarkDao
import com.haejung.snapmark.data.source.local.MarkDatabase
import com.haejung.snapmark.data.source.local.MarkPresetDao
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@MediumTest
class MarkAndPresetTest {
    private lateinit var markDao: MarkDao
    private lateinit var markPresetDao: MarkPresetDao
    private lateinit var db: MarkDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MarkDatabase::class.java
        ).build()
        markDao = db.markDao()
        markPresetDao = db.markPresetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeMarkAndReadInList() {
        val markSize = 2
        val mark = createMarkList("test", markSize)
        markDao.insertAll(*mark.toTypedArray())
        val byAll = markDao.getAll()

        assertThat(byAll.size, equalTo(markSize))
    }

    @Test
    @Throws(Exception::class)
    fun writeMarkPresetAndRead() {
        val name = "testPreset"
        val preset = createMarkPresetWithMark(name, null)
        markPresetDao.insertAll(preset)
        val foundPreset = markPresetDao.getByName(name)

        assertThat(foundPreset.name, equalTo(name))
    }

    @Test
    @Throws(Exception::class)
    fun writeCombineMarkAndPreset() {
        val markName = "test"
        val markPresetName = "testPreset"
        markDao.insertAll(createMark(markName))
        val foundMark = markDao.getByName(markName)

        val preset = createMarkPresetWithMark(markPresetName, foundMark)
        markPresetDao.insertAll(preset)
        val foundPreset = markPresetDao.getByName(markPresetName)

        assertThat(foundPreset.markId, equalTo(foundMark.id))
    }

    @Test
    @Throws(Exception::class)
    fun writeCombineMarkAndMultiplePreset() {
        val markName = "test"
        val markPresetName = "testPreset"
        val size = 2
        markDao.insertAll(createMark(markName))
        val foundMark = markDao.getByName(markName)

        val preset = createMarkPresetListWithMark(markPresetName, size, foundMark)
        markPresetDao.insertAll(*preset.toTypedArray())

        val foundPresets = markPresetDao.getAllByMarkName(markName)

        for (foundPreset in foundPresets) {
            assertThat(foundPreset.markId, equalTo(foundMark.id))
            assertThat(foundPreset.name, equalTo(markName))
        }
        assertThat(foundPresets.size, equalTo(size))
    }

}
