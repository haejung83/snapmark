package com.haejung.snapmark.presentation.snap

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.SnapFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_ID
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_MARK
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_PRESET
import timber.log.Timber

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Snackbar
        viewDataBinding.viewmodel?.snackbarMessage?.let {
            view.setupSnackbar(
                this@SnapFragment,
                it, Snackbar.LENGTH_SHORT
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        arguments?.let {
            when (it.getString(EXTRA_TYPE)) {
                EXTRA_TYPE_MARK -> viewDataBinding.viewmodel?.initWithMarkId(it.getInt(EXTRA_ID))
                EXTRA_TYPE_PRESET -> viewDataBinding.viewmodel?.initWithPresetId(it.getInt(EXTRA_ID))
                else -> Timber.w("No exists argument for launch SnapFragment")
            }
        }

        // FIXME: Temporary
        openGallery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_MULTI_IMAGE -> {
                data?.let {
                    Timber.d("onActivityResult: ${data.data} or ${data.clipData}")
                }
                viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
            }
        }
    }

    private fun openGallery() {
        startActivityForResult(
            Intent.createChooser(
                Intent().apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    action = Intent.ACTION_GET_CONTENT
                }, getString(R.string.title_gallery_picking_multi_images)
            ), REQUEST_PICK_MULTI_IMAGE
        )
    }

    companion object {
        const val REQUEST_PICK_MULTI_IMAGE = 2

        fun newInstance() = SnapFragment()
    }

}
