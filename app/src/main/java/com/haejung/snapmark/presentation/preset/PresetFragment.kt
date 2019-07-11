package com.haejung.snapmark.presentation.preset

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.PresetFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.presentation.base.DataBindingNavigationFragment
import timber.log.Timber

class PresetFragment private constructor() : DataBindingNavigationFragment<PresetFragmentBinding>() {

    override val layoutResId: Int
        get() = R.layout.preset_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = (activity as AppCompatActivity).obtainViewModel(PresetViewModel::class.java).apply {
            // Binding here
        }
    }

    override fun onNavigationSelected() {
        takeFabOwnership()
    }

    private fun takeFabOwnership() {
        Timber.i("takeFabOwnership")
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_mark)?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(R.drawable.ic_add_black_24dp)
            it.setOnClickListener {
                Timber.i("Clicked the Fab")
            }
        }
    }

    companion object {
        fun newInstance() = PresetFragment()
    }

}
