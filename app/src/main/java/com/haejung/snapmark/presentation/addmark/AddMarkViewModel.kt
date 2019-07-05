package com.haejung.snapmark.presentation.addmark

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.data.source.repository.MarkRepository
import com.haejung.snapmark.extend.createScaledBitmapWithAspectRatio
import com.haejung.snapmark.presentation.Event
import com.haejung.snapmark.presentation.base.DisposableViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AddMarkViewModel(
    private val markRepository: MarkRepository
) : DisposableViewModel() {

    private val _selectedMark = MutableLiveData<Bitmap>()
    val selectedMark: LiveData<Bitmap>
        get() = _selectedMark

    private val _saveMarkEvent = MutableLiveData<Event<Unit>>()
    val saveMarkEvent: LiveData<Event<Unit>>
        get() = _saveMarkEvent

    private val _openGalleryEvent = MutableLiveData<Event<Unit>>()
    val openGalleryEvent: LiveData<Event<Unit>>
        get() = _openGalleryEvent

    val addMarkActionListener = object : AddMarkActionListener {
        override fun onClick(action: AddMarkActionListener.Action?) {
            when (action) {
                AddMarkActionListener.Action.ACTION_SELECT_IMAGE -> openGallery()
                AddMarkActionListener.Action.ACTION_SAVE -> saveMark()
            }
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        Timber.d("handleActivityResult: $requestCode, $resultCode")
    }

    fun openGallery() {
        _openGalleryEvent.value = Event(Unit)
    }

    fun setSelectedImage(image: Bitmap) {
        _selectedMark.value = image
    }

    fun saveMark() {
        selectedMark.value?.let {
            resizeMarkBitmapIfNeed(it) { resizedBitmap ->
                markRepository
                    .insertAll(Mark(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), resizedBitmap))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _saveMarkEvent.value = Event(Unit)
                    }, { t ->
                        Timber.e("Failed to save mark image: $t")
                    })
                    .addTo(disposable)
            }
        }
    }

    private fun resizeMarkBitmapIfNeed(bitmap: Bitmap, block: (Bitmap) -> Unit) {
        Single.fromCallable {
            if (bitmap.width * bitmap.height > MAX_SIZE_OF_BOTH_SIDE * MAX_SIZE_OF_BOTH_SIDE) {
                bitmap.createScaledBitmapWithAspectRatio(MAX_SIZE_OF_BOTH_SIDE)
            } else {
                bitmap
            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe({ resizedBitmap ->
                block(resizedBitmap)
            }, {
                Timber.e("Couldn't resize bitmap: $it")
            })
            .addTo(disposable)
    }

    companion object {
        private const val MAX_SIZE_OF_BOTH_SIDE = 512
    }

}
