package com.haejung.snapmark.presentation.sns

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.SnsFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.presentation.base.DataBindingNavigationFragment
import timber.log.Timber

class SnsFragment private constructor() : DataBindingNavigationFragment<SnsFragmentBinding>() {

    override val layoutResId: Int
        get() = R.layout.sns_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = (activity as AppCompatActivity).obtainViewModel(SnsViewModel::class.java).apply {
        }
    }

    override fun onNavigationSelected() {
        takeFabOwnership()
    }

    private fun takeFabOwnership() {
        Timber.i("takeFabOwnership")
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_mark)?.let {
            it.visibility = View.GONE
            it.setImageResource(android.R.color.transparent)
            it.setOnClickListener(null)
        }
    }

    companion object {
        fun newInstance() = SnsFragment()
    }

}
