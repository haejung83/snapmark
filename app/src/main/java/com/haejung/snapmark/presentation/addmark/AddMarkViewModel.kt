package com.haejung.snapmark.presentation.addmark

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AddMarkViewModel(
    private val markRepository: MarkRepository
) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    // Bi-Directional Binding
    val selectedMark = MutableLiveData<Bitmap>()

    private val _saveMarkEvent = MutableLiveData<Event<Unit>>()
    val saveMarkEvent: LiveData<Event<Unit>>
        get() = _saveMarkEvent

    private val _openGalleryEvent = MutableLiveData<Event<Unit>>()
    val openGalleryEvent: LiveData<Event<Unit>>
        get() = _openGalleryEvent

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        Timber.d("handleActivityResult: $requestCode, $resultCode")
    }

    fun saveMark() {
        selectedMark.value?.let {
            markRepository
                .insertAll(Mark(it, it))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _saveMarkEvent.value = Event(Unit)
                }, { t ->
                    Timber.e("Show an error: $t")
                })
                .addTo(disposable)
        }
    }

    fun openGallery() {
        _openGalleryEvent.value = Event(Unit)
    }

}
