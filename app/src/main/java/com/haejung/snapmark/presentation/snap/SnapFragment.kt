package com.haejung.snapmark.presentation.snap

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.SnapFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_ID
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_MARK
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_PRESET
import kotlinx.android.synthetic.main.snap_fragment.*
import timber.log.Timber
import java.io.File

class SnapFragment : Fragment() {

    private lateinit var viewDataBinding: SnapFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = SnapFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as AppCompatActivity).obtainViewModel(SnapViewModel::class.java).also {
                // Bind here
                it.openGalleryEvent.observe(this@SnapFragment, Observer { event ->
                    event.getContentIfNotHandled()?.let { openGallery() }
                })
                it.saveTargetImageEvent.observe(this@SnapFragment, Observer { event ->
                    event.getContentIfNotHandled()?.let { saveTargetImageWithMark() }
                })
                it.markLoadedEvent.observe(this@SnapFragment, Observer { event ->
                    event.getContentIfNotHandled()?.let { mark -> snapEditView.mark = mark }
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_MULTI_IMAGE -> {
                data?.let {
                    Timber.d("onActivityResult: ${data.data} or ${data.clipData}")
                    data.data?.let { setImageTargetSource(it) }
                    data.clipData?.let { setImageTargetSource(it.getItemAt(0).uri) }
                }
                viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
            }
        }
    }

    private fun setImageTargetSource(source: Uri) {
        val targetImageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, source)
        targetImageBitmap?.let {
            snapEditView.targetImage = it
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

    private fun saveTargetImageWithMark() {
        Timber.i("saveTargetImageWithMark")
        if (checkPermission()) {
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES + "/test.png"
            snapEditView.save(path)
        }
    }

    private fun checkPermission(): Boolean {
        activity?.let {
            val required = Manifest.permission.WRITE_EXTERNAL_STORAGE
            val grantedResult = ContextCompat.checkSelfPermission(it, required)
            if (grantedResult != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(it, required)) {
                    showPermissionRationaleDialog(it, required)
                } else {
                    requestSinglePermission(it, required)
                }
            } else {
                // Granted case
                return true
            }
        }
        return false
    }

    private fun requestSinglePermission(activity: Activity, required: String) {
        ActivityCompat.requestPermissions(activity, arrayOf(required), REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveTargetImageWithMark()
                }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showPermissionRationaleDialog(activity: Activity, required: String) {
        val buttonHandler = DialogInterface.OnClickListener { dialog, which ->
            requestSinglePermission(activity, required)
            dialog.dismiss()
        }
        MaterialAlertDialogBuilder(context, R.style.Widget_Shrine_MaterialAlertDialog).apply {
            setTitle(R.string.title_dialog_permission_title)
            setMessage(R.string.title_dialog_permission_write_external_storage)
            setPositiveButton(R.string.title_ok, buttonHandler)
        }.show()
    }

    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 100
        const val REQUEST_PICK_MULTI_IMAGE = 2

        fun newInstance() = SnapFragment()
    }

}
