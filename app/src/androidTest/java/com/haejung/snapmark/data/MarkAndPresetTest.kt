package com.haejung.snapmark.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haejung.snapmark.data.source.local.MarkDao
import com.haejung.snapmark.data.source.local.MarkDatabase
import com.haejung.snapmark.data.source.local.MarkPresetDao
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MarkAndPresetTest {
    private lateinit var markDao: MarkDao
    private lateinit var markPresetDao: MarkPresetDao
    private lateinit var db: MarkDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MarkDatabase::class.java
        ).allowMainThreadQueries().build()
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
        val insertMark = markDao.insertAll(*mark.toTypedArray())
        insertMark.test()
            .assertSubscribed()
            .assertComplete()

        val foundMark = markDao.getAll()
        foundMark.test()
            .assertSubscribed()
            .assertValue {
                it.size == markSize
            }
    }

    @Test
    @Throws(Exception::class)
    fun writeMarkPresetAndRead() {
        val name = "testPreset"
        val preset = createMarkPresetWithMark(name, null)
        var insertPreset = markPresetDao.insertAll(preset)
        insertPreset.test()
            .assertSubscribed()
            .assertComplete()

        val foundPreset = markPresetDao.getByName(name)
        foundPreset.test()
            .assertSubscribed()
            .assertValue {
                it.name == name
            }
    }

    @Test
    @Throws(Exception::class)
    fun writeCombineMarkAndPreset() {
        val markName = "test"
        val markPresetName = "testPreset"
        val insertMark = markDao.insertAll(createMark(markName))
        insertMark.test()
            .assertSubscribed()
            .assertComplete()

        val foundMark = markDao.getByName(markName)
        val foundMarkTest = foundMark.test()
        foundMarkTest
            .assertSubscribed()
            .assertValue {
                it.name == markName
            }
        val actualMark = foundMarkTest.values().first()

        val preset = createMarkPresetWithMark(markPresetName, actualMark)
        val insertMarkPreset = markPresetDao.insertAll(preset)
        insertMarkPreset.test()
            .assertSubscribed()
            .assertComplete()

        val foundPreset = markPresetDao.getByName(markPresetName)
        foundPreset.test()
            .assertSubscribed()
            .assertValue {
                it.markId == actualMark.id
            }
    }

    @Test
    @Throws(Exception::class)
    fun writeCombineMarkAndMultiplePreset() {
        val markName = "test"
        val markPresetName = "testPreset"
        val subMarkPresetName = "testSubPreset"
        val size = 2

        val insertMark = markDao.insertAll(createMark(markName))
        insertMark.test()
            .assertSubscribed()
            .assertComplete()

        val foundMark = markDao.getByName(markName)
        val foundMarkTest = foundMark.test()
        foundMarkTest
            .assertSubscribed()
            .assertValue {
                it.name == markName
            }
        val actualMark = foundMarkTest.values().first()

        val preset = createMarkPresetListWithMark(markPresetName, size, actualMark)
        val insertMarkPreset = markPresetDao.insertAll(*preset.toTypedArray())
        insertMarkPreset.test()
            .assertSubscribed()
            .assertComplete()

        val subPreset = createMarkPresetListWithMark(subMarkPresetName, size, null)
        val insertMarkPresetWithoutMark = markPresetDao.insertAll(*subPreset.toTypedArray())
        insertMarkPresetWithoutMark.test()
            .assertSubscribed()
            .assertComplete()

        val foundPresets = markPresetDao.getAllByMarkName(markName)
        foundPresets.test()
            .assertSubscribed()
            .assertValue { list ->
                list.size == size
            }
            .assertValue { list ->
                list.all { it.markId == actualMark.id }
            }
            .assertValue { list ->
                list.all { it.name.startsWith(markPresetName) }
            }
    }

}
