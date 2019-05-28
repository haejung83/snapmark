package com.haejung.snapmark.presentation.mark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MarkViewModel(
    private val markRepository: MarkRepository
) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val _items = MutableLiveData<List<Mark>>().apply { value = emptyList() }
    val items: LiveData<List<Mark>>
        get() = _items

    private val _newMarkEvent = MutableLiveData<Event<Unit>>()
    val newMarkEvent: LiveData<Event<Unit>>
        get() = _newMarkEvent

    private val _snapEvent = MutableLiveData<Event<Int>>()
    val snapEvent: LiveData<Event<Int>>
        get() = _snapEvent

    private val _createPresetEvent = MutableLiveData<Event<Int>>()
    val createPresetEvent: LiveData<Event<Int>>
        get() = _createPresetEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarMessage

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        Timber.d("handleActivityResult: $requestCode, $resultCode")
    }

    fun start() {
        loadMarks()
    }

    private fun loadMarks() {
        Timber.d("loadMarks")
        markRepository.getMarks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("Mark Loaded: ${it.size}")
                _items.value = it
            }.addTo(disposable)
    }

    fun addMark() {
        _newMarkEvent.value = Event(Unit)
    }

    fun removeMark(mark: Mark) {
        Timber.d("removeMark: ${mark.id}")
        markRepository.deleteMarkById(mark.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _snackbarMessage.value = Event(R.string.title_snackbar_msg_removed)
            }
            .addTo(disposable)
    }

    fun snap(mark: Mark) {
        _snapEvent.value = Event(mark.id)
    }

    fun createPreset(mark: Mark) {
        _createPresetEvent.value = Event(mark.id)
    }

}

