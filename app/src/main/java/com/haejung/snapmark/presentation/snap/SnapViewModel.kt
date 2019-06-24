package com.haejung.snapmark.presentation.snap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkPresetRepository
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SnapViewModel(
    private val markRepository: MarkRepository,
    private val markPresetRepository: MarkPresetRepository
) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarMessage

    private val _openGalleryEvent = MutableLiveData<Event<Unit>>()
    val openGalleryEvent: LiveData<Event<Unit>>
        get() = _openGalleryEvent

    private val _saveTargetImageEvent = MutableLiveData<Event<Unit>>()
    val saveTargetImageEvent: LiveData<Event<Unit>>
        get() = _saveTargetImageEvent

    private val _markLoadedEvent = MutableLiveData<Event<Mark>>()
    val markLoadedEvent: LiveData<Event<Mark>>
        get() = _markLoadedEvent

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        Timber.d("handleActivityResult: $requestCode, $resultCode")
    }

    fun initWithMarkId(markId: Int) {
        Timber.d("initWithMarkId: $markId")
        markRepository.getMarkById(markId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // do something here
                Timber.d("Loaded mark with $markId")
                _markLoadedEvent.value = Event(it)
            }
            .addTo(disposable)
    }

    fun initWithPresetId(presetId: Int) {
        Timber.d("initWithPresetId: $presetId")
        markPresetRepository.getMarkPresetDetailViewById(presetId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // do something here
                Timber.d("Loaded preset with $presetId")
            }
            .addTo(disposable)
    }

    fun openGalleryForPicking() {
        _openGalleryEvent.value = Event(Unit)
    }

    fun saveTargetImageWithMark() {
        _saveTargetImageEvent.value = Event(Unit)
    }

}
