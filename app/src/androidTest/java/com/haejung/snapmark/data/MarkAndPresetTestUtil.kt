package com.haejung.snapmark.data

import android.graphics.Bitmap
import android.graphics.Matrix

private val fakeImageThumbnail = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
private val fakeImage = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
private val fakeMatrix = Matrix()

fun createMark(name: String) =
    Mark(fakeImageThumbnail, fakeImage, name)

fun createMarkList(prefixName: String, size: Int = 1): List<Mark> =
    mutableListOf<Mark>().apply {
        for (index in 0 until size) {
            add(
                Mark(
                    fakeImageThumbnail,
                    fakeImage,
                    prefixName.plus("_$index")
                )
            )
        }
    }

fun createMarkPresetWithMark(name: String, mark: Mark?) =
    MarkPreset(
        fakeMatrix,
        mark?.id,
        name
    )

fun createMarkPresetListWithMark(prefixName: String, size: Int, mark: Mark?) =
    mutableListOf<MarkPreset>().apply {
        for (index in 0 until size) {
            add(createMarkPresetWithMark(prefixName.plus("_$index"), mark))
        }
    }

