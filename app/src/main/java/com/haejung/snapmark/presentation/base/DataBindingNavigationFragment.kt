package com.haejung.snapmark.presentation.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding

abstract class DataBindingNavigationFragment<T : ViewDataBinding> : DataBindingFragment<T>() {

    private var isPendingOnNavigationSelected = false

    fun navigationSelected() {
        if (isAdded)
            onNavigationSelected()
        else
            isPendingOnNavigationSelected = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isPendingOnNavigationSelected) {
            isPendingOnNavigationSelected = false
            onNavigationSelected()
        }
    }

    abstract fun onNavigationSelected()

}