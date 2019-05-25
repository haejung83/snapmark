package com.haejung.snapmark.presentation.preset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.obtainViewModel

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
        viewModel = (activity as AppCompatActivity).obtainViewModel(PresetViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
