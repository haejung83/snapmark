package com.haejung.snapmark.data.source

import com.haejung.snapmark.data.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MarkDataSource {

    fun getMarks(): Flowable<List<Mark>>

    fun getMarkById(): Flowable<Mark>

    fun insertAll(vararg marks: Mark): Single<Int>

    fun update(mark: Mark): Completable

    fun deleteMarkById(id: Int): Completable

    fun deleteMarkAll(): Single<Int>

}