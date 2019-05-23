package com.haejung.snapmark.presentation.sns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.haejung.snapmark.R

class SnsFragment : Fragment() {

    companion object {
        fun newInstance() = SnsFragment()
    }

    private lateinit var viewModel: SnsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sns_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SnsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}