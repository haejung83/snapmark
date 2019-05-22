package com.haejung.snapmark.data.source.repository

import com.haejung.snapmark.data.MarkPreset
import com.haejung.snapmark.data.MarkPresetDetailView
import com.haejung.snapmark.data.source.local.MarkPresetDao
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class MarkPresetRepository private constructor(
    private val markPresetDao: MarkPresetDao
) : MarkPresetDataSource {

    override fun getMarkPresets(): Flowable<List<MarkPreset>> =
        markPresetDao.getAll()

    override fun getMarkPresetById(id: Int): Flowable<MarkPreset> =
        markPresetDao.getById(id)

    override fun getMarkPresetsByMarkId(markId: Int): Flowable<List<MarkPreset>> =
        markPresetDao.getAllByMarkId(markId)

    override fun getMarkPresetsByMarkName(markName: String): Flowable<List<MarkPreset>> =
        markPresetDao.getAllByMarkName(markName)

    override fun getMarkPresetDetailViewAll(): Flowable<List<MarkPresetDetailView>> =
        markPresetDao.getDetailViewAll()

    override fun getMarkPresetDetailViewById(id: Int): Flowable<MarkPresetDetailView> =
        markPresetDao.getDetailViewById(id)

    override fun getMarkPresetDetailViewByName(name: String): Flowable<MarkPresetDetailView> =
        markPresetDao.getDetailViewByName(name)

    override fun insertAll(vararg markPresets: MarkPreset): Completable =
        markPresetDao.insertAll(*markPresets)

    override fun update(markPreset: MarkPreset): Completable =
        markPresetDao.update(markPreset)

    override fun deleteMarkPresetById(id: Int): Completable =
        markPresetDao.deleteById(id)

    override fun deleteMarkPresets(): Single<Int> =
        markPresetDao.deleteAll()

    companion object {
        private var instance: MarkPresetRepository? = null

        fun getInstance(markPresetDao: MarkPresetDao): MarkPresetRepository =
            instance ?: synchronized(this) {
                instance ?: MarkPresetRepository(markPresetDao).apply {
                    instance = this
                }
            }
    }

}