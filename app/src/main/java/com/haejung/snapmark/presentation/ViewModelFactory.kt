package com.haejung.snapmark.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haejung.snapmark.data.source.provideMarkPresetRepository
import com.haejung.snapmark.data.source.provideMarkRepository
import com.haejung.snapmark.data.source.repository.MarkPresetRepository
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.addmark.AddMarkViewModel
import com.haejung.snapmark.presentation.mark.MarkViewModel
import com.haejung.snapmark.presentation.preset.PresetViewModel
import com.haejung.snapmark.presentation.snap.SnapViewModel
import com.haejung.snapmark.presentation.sns.SnsViewModel

class ViewModelFactory private constructor(
    private val markRepository: MarkRepository,
    private val markPresetRepository: MarkPresetRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MarkViewModel::class.java) -> MarkViewModel(markRepository)
                isAssignableFrom(PresetViewModel::class.java) -> PresetViewModel(markPresetRepository)
                isAssignableFrom(SnsViewModel::class.java) -> SnsViewModel()
                isAssignableFrom(AddMarkViewModel::class.java) -> AddMarkViewModel(markRepository)
                isAssignableFrom(SnapViewModel::class.java) -> SnapViewModel(markRepository, markPresetRepository)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T

    companion object {
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    provideMarkRepository(context),
                    provideMarkPresetRepository(context)
                ).apply {
                    instance = this
                }
            }
    }

}