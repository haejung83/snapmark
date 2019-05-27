package com.haejung.snapmark.presentation.mark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

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
        empty.value?.let { if (it) loadMarks() }
    }

    private fun loadMarks() {
        Timber.d("loadMarks")
        disposable.add(
            markRepository.getMarks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.d("Loaded")
                    _items.value = it
                })
    }

    fun addMark() {
        Timber.d("Add Mark")
        _newMarkEvent.value = Event(Unit)
    }

    fun snap(mark: Mark) {
        Timber.d("Snap: ${mark.id}")
        _snapEvent.value = Event(mark.id)
    }

    fun showMenu(mark: Mark) {
        Timber.d("Show Menu: ${mark.id}")
    }

}
