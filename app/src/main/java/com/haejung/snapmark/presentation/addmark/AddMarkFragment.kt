package com.haejung.snapmark.presentation.addmark

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.AddMarkFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.presentation.Event
import com.haejung.snapmark.presentation.base.DataBindingFragment

class AddMarkFragment private constructor() : DataBindingFragment<AddMarkFragmentBinding>() {

    override val layoutResId: Int
        get() = R.layout.add_mark_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = (activity as AppCompatActivity).obtainViewModel(AddMarkViewModel::class.java).also {
            it.openGalleryEvent.observe(this@AddMarkFragment, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    openGallery()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                data?.let {
                    viewDataBinding.viewmodel?.setSelectedImage(
                        MediaStore.Images.Media.getBitmap(activity?.contentResolver, it.data)
                    )
                }
            }
            else -> viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
        }
    }

    private fun openGallery() {
        startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_PICK_IMAGE)
    }

    companion object {
        const val REQUEST_PICK_IMAGE = 1

        fun newInstance() = AddMarkFragment()
    }

}
