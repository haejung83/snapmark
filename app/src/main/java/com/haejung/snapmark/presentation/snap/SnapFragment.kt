package com.haejung.snapmark.presentation.snap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.haejung.snapmark.databinding.SnapFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel

class SnapFragment : Fragment() {

    private lateinit var viewDataBinding: SnapFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = SnapFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as AppCompatActivity).obtainViewModel(SnapViewModel::class.java).also {
                // Bind here
            }
        }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
    }

    companion object {
        fun newInstance() = SnapFragment()
    }

}
