package com.haejung.snapmark.data.source.repository

import com.haejung.snapmark.data.MarkPreset
import com.haejung.snapmark.data.MarkPresetDetailView
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MarkPresetDataSource {

    fun getMarkPresets(): Flowable<List<MarkPreset>>

    fun getMarkPresetById(id: Int): Flowable<MarkPreset>

    fun getMarkPresetsByMarkId(markId: Int): Flowable<List<MarkPreset>>

    fun getMarkPresetsByMarkName(markName: String): Flowable<List<MarkPreset>>

    fun getMarkPresetDetailViewAll(): Flowable<List<MarkPresetDetailView>>

    fun getMarkPresetDetailViewById(id: Int): Flowable<MarkPresetDetailView>

    fun getMarkPresetDetailViewByName(name: String): Flowable<MarkPresetDetailView>

    fun insertAll(vararg markPresets: MarkPreset): Completable

    fun update(markPreset: MarkPreset): Completable

    fun deleteMarkPresetById(id: Int): Completable

    fun deleteMarkPresets(): Single<Int>

}