package com.haejung.snapmark.data

private val fakeImageThumbnail = byteArrayOf(0, 0)
private val fakeImage = byteArrayOf(0, 0, 0, 0)

private val fakePosition = byteArrayOf(0, 0, 0, 0)
private val fakeScale = byteArrayOf(0, 0, 0, 0)
private val fakeRotation = byteArrayOf(0, 0, 0, 0)

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

fun createMarkMatrix() =
    MarkMatrix(
        fakePosition,
        fakeScale,
        fakeRotation
    )

fun createMarkPresetWithMark(name: String, mark: Mark?) =
    MarkPreset(
        createMarkMatrix(),
        mark?.id,
        name
    )

fun createMarkPresetListWithMark(prefixName: String, size: Int, mark: Mark?) =
    mutableListOf<MarkPreset>().apply {
        for (index in 0 until size) {
            add(createMarkPresetWithMark(prefixName.plus("_$index"), mark))
        }
    }

