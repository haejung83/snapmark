package com.haejung.snapmark.presentation.mark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.MarkFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.Event
import com.haejung.snapmark.presentation.addmark.AddMarkActivity
import timber.log.Timber

class MarkFragment private constructor() : Fragment() {

    private lateinit var viewDataBinding: MarkFragmentBinding
    private lateinit var markListAdapter: MarkListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = MarkFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as AppCompatActivity).obtainViewModel(MarkViewModel::class.java).also {
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
            }
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Snackbar
        viewDataBinding.viewmodel?.snackbarMessage?.let {
            view.setupSnackbar(
                this@MarkFragment,
                it, Snackbar.LENGTH_SHORT
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupFab()
        setupListAdapter()
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

    private fun setupListAdapter() {
        viewDataBinding.viewmodel?.let {
            markListAdapter = MarkListAdapter(it)
            viewDataBinding.recyclerView.adapter = markListAdapter
            viewDataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadMarks() {
        viewDataBinding.viewmodel?.start()
    }

    private fun openNewMark() {
        startActivity(Intent(context, AddMarkActivity::class.java))
    }

    private fun openSnap(markId: Int) {
        // TODO: Not implemented yet
        Timber.d("Snap with $markId")
    }

    private fun openCreatePreset(markId: Int) {
        // TODO: Not implemented yet
        Timber.d("Create Preset with $markId")
    }

    companion object {
        fun newInstance() = MarkFragment()
    }

}
