package com.haejung.snapmark.presentation.mark

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.MarkFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.Event
import com.haejung.snapmark.presentation.addmark.AddMarkActivity
import com.haejung.snapmark.presentation.base.DataBindingFragment
import com.haejung.snapmark.presentation.snap.SnapActivity
import timber.log.Timber

class MarkFragment private constructor() : DataBindingFragment<MarkFragmentBinding>() {

    override val layoutResId: Int
        get() = R.layout.mark_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = (activity as AppCompatActivity).obtainViewModel(MarkViewModel::class.java).also {
            // New Mark
            it.newMarkEvent.observe(this@MarkFragment, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    openNewMark()
                }
            })
            // Snap
            it.snapEvent.observe(this@MarkFragment, Observer<Event<Int>> { event ->
                event.getContentIfNotHandled()?.let { markId ->
                    openSnap(markId)
                }
            })
            // Create Preset
            it.createPresetEvent.observe(this@MarkFragment, Observer<Event<Int>> { event ->
                event.getContentIfNotHandled()?.let { markId ->
                    openCreatePreset(markId)
                }
            })
            // Snackbar
            view?.setupSnackbar(this@MarkFragment, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }
        setupFab()
        loadMarks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_mark)?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(R.drawable.ic_add_black_24dp)
            it.setOnClickListener {
                viewDataBinding.viewmodel?.addMark()
            }
        }
    }

    private fun loadMarks() {
        viewDataBinding.viewmodel?.start()
    }

    private fun openNewMark() {
        startActivity(Intent(context, AddMarkActivity::class.java))
    }

    private fun openSnap(markId: Int) {
        startActivity(Intent(context, SnapActivity::class.java).apply {
            putExtras(SnapActivity.createBundleWithMark(markId))
        })
    }

    private fun openCreatePreset(markId: Int) {
        // TODO: Not implemented yet
        Timber.d("Create Preset with $markId")
    }

    companion object {
        fun newInstance() = MarkFragment()
    }

}
