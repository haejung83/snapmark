package com.haejung.snapmark.presentation.snap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkPresetRepository
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.presentation.Event
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import timber.log.Timber

class SnapViewModel(
    private val markRepository: MarkRepository,
    private val markPresetRepository: MarkPresetRepository
) : ViewModel() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val _currentSnap = MutableLiveData<Snap>()
    val currentSnap: LiveData<Snap>
        get() = _currentSnap

    private val _currentMark = MutableLiveData<Mark>()
    val currentMark: LiveData<Mark>
        get() = _currentMark

    private val _snapList = MutableLiveData<List<Snap>>().apply { value = emptyList() }
    val snapList: LiveData<List<Snap>>
        get() = _snapList

    private val _saveRequestEvent = MutableLiveData<Event<Unit>>()
    val saveRequestEvent: LiveData<Event<Unit>>
        get() = _saveRequestEvent

    private val _showFinishDialogEvent = MutableLiveData<Event<Unit>>()
    val showFinishDialogEvent: LiveData<Event<Unit>>
        get() = _showFinishDialogEvent

    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarMessage

    val snapActionListener = object : SnapActionListener {
        override fun onClick(action: SnapActionListener.Action, snap: Snap) {
            when (action) {
                SnapActionListener.Action.ACTION_IMAGE_TARGET_SELECT -> {
                    Timber.i("action select: $snap")
                    currentSnap(snap)
                }
                SnapActionListener.Action.ACTION_IMAGE_TARGET_DELETE -> {
                    Timber.i("action delete: $snap")
                    removeSnap(snap)
                }
            }
        }
    }

    private var saveSubscription: Subscription? = null

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
                Timber.d("Loaded mark with $markId")
                _currentMark.value = it
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

    fun addSnap(vararg snaps: Snap) {
        val oldSnapList = _snapList.value ?: emptyList()
        _snapList.value = mutableListOf(*oldSnapList.toTypedArray(), *snaps)
    }

    fun removeSnap(vararg snaps: Snap) {
        val oldSnapList = _snapList.value ?: emptyList()
        _snapList.value = mutableListOf(*oldSnapList.toTypedArray()).apply {
            for (snap in snaps) remove(snap)
        }
    }

    fun currentSnap(snap: Snap) {
        _currentSnap.value = snap
    }

    // FIXME: [Saving] Freaky. Make it more elegance.
    fun saveSnapImage(completableSnapImage: Completable) {
        completableSnapImage
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.i("Save one")
                // Request next one
                saveSubscription?.request(1)
            }
            .addTo(disposable)
    }

    // FIXME: [Saving] Freaky. Make it more elegance.
    fun saveSnapImages() {
        Flowable.fromIterable(_snapList.value)
            .subscribe({
                currentSnap(it)
                _saveRequestEvent.value = Event(Unit)
            }, {
                Timber.e(it)
                saveSubscription = null
                _snackbarMessage.value = Event(R.string.title_snackbar_msg_save_snap_image_fail)
            }, {
                Timber.i("Save done")
                saveSubscription = null
                _showFinishDialogEvent.value = Event(Unit)
            }, {
                saveSubscription = it
                it.request(1)
            })
            .addTo(disposable)
    }

}
