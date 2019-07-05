package com.haejung.snapmark.presentation.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class DisposableViewModel : ViewModel() {

    protected val disposable by lazy(LazyThreadSafetyMode.NONE) {
        CompositeDisposable()
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

}