package com.haejung.snapmark.data.source.repository

import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.local.MarkDao
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class MarkRepository(
    private val markDao: MarkDao
) : MarkDataSource {

    override fun getMarks(): Flowable<List<Mark>> =
        markDao.getAll()

    override fun getMarkById(id: Int): Flowable<Mark> =
        markDao.getById(id)

    override fun insertAll(vararg marks: Mark): Completable =
        markDao.insertAll(*marks)

    override fun update(mark: Mark): Completable =
        markDao.update(mark)

    override fun deleteMarkById(id: Int): Completable =
        markDao.deleteById(id)

    override fun deleteMarkAll(): Single<Int> =
        markDao.deleteAll()

}