package com.haejung.snapmark.extend

import java.nio.ByteBuffer

fun ByteArray.toFloatArray(): FloatArray =
    FloatArray(size / 4).also {
        ByteBuffer.wrap(this).asFloatBuffer().get(it)
    }

fun FloatArray.toByteArray(): ByteArray =
    ByteBuffer.allocate(size * 4).also { byteBuffer ->
        forEach { byteBuffer.putFloat(it) }
    }.array()
