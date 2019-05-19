package com.haejung.snapmark.data.source

import com.haejung.snapmark.data.Mark
import io.reactivex.Flowable

interface MarkDataSource {

    fun getMarks(): Flowable<Mark>

}