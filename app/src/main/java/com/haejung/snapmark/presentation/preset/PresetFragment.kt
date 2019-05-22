package com.haejung.snapmark.presentation.preset

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.haejung.snapmark.R

class PresetFragment : Fragment() {

    companion object {
        fun newInstance() = PresetFragment()
    }

    private lateinit var viewModel: PresetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.preset_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PresetViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
