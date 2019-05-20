package com.haejung.snapmark.data.source

import com.haejung.snapmark.data.MarkPreset
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MarkPresetDataSource {

    fun getMarkPresets(): Flowable<List<MarkPreset>>

    fun getMarkPresetById(id: Int): Flowable<MarkPreset>

    fun getMarkPresetsByMarkId(markId: Int): Flowable<List<MarkPreset>>

    fun getMarkPresetsByMarkName(markName: String): Flowable<List<MarkPreset>>

    fun insertAll(vararg markPresets: MarkPreset): Single<Int>

    fun update(markPreset: MarkPreset): Completable

    fun deleteMarkPresetById(id: Int): Completable

    fun deleteMarkPresets(): Single<Int>

}