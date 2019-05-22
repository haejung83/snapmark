package com.haejung.snapmark.presentation.mark

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.haejung.snapmark.R

class MarkFragment : Fragment() {

    companion object {
        fun newInstance() = MarkFragment()
    }

    private lateinit var viewModel: MarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mark_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarkViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
