package com.haejung.snapmark.data.source

import android.content.Context
import com.haejung.snapmark.data.source.local.MarkDatabase
import com.haejung.snapmark.data.source.repository.MarkPresetRepository
import com.haejung.snapmark.data.source.repository.MarkRepository

fun provideMarkRepository(context: Context): MarkRepository =
    MarkDatabase.getInstance(context).run {
        MarkRepository.getInstance(markDao())
    }

fun provideMarkPresetRepository(context: Context): MarkPresetRepository =
    MarkDatabase.getInstance(context).run {
        MarkPresetRepository.getInstance(markPresetDao())
    }
