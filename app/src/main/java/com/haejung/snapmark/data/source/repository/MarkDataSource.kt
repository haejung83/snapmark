package com.haejung.snapmark.data.source.repository

import com.haejung.snapmark.data.Mark
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MarkDataSource {

    fun getMarks(): Flowable<List<Mark>>

    fun getMarkById(): Flowable<Mark>

    fun insertAll(vararg marks: Mark): Completable

    fun update(mark: Mark): Completable

    fun deleteMarkById(id: Int): Completable

    fun deleteMarkAll(): Single<Int>

}