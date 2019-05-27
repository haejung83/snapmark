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
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.databinding.MarkFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
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
                        Timber.d("Processing Add New Event")
                        openNewMark()
                    }
                })
                // Snap
                it.snapEvent.observe(this@MarkFragment, Observer<Event<Int>> { event ->
                    event.getContentIfNotHandled()?.let {
                        Timber.d("Processing Snap Event")
                    }
                })
            }
        }

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupFab()
        setupListAdapter()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
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

    private fun openNewMark() {
        startActivity(Intent(context, AddMarkActivity::class.java))
    }

    interface MarkActionListener {
        enum class Action {
            ACTION_SNAP,
            ACTION_OPEN_MENU
        }

        fun onClick(mark: Mark, action: Action?)
    }

    companion object {
        fun newInstance() = MarkFragment()
    }

}
