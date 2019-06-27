package com.haejung.snapmark.presentation.snap

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStorageDirectory
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.databinding.SnapFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.snap.SnapActionListener.Action
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_ID
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_MARK
import com.haejung.snapmark.presentation.snap.SnapActivity.Companion.EXTRA_TYPE_PRESET
import io.reactivex.Completable
import kotlinx.android.synthetic.main.snap_fragment.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SnapFragment : Fragment() {

    private lateinit var mediaScanner: MediaScannerConnection
    private lateinit var viewDataBinding: SnapFragmentBinding
    private lateinit var snapListAdapter: SnapListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = SnapFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as AppCompatActivity).obtainViewModel(SnapViewModel::class.java).also {
                // Bind here
                it.markLoadedEvent.observe(this@SnapFragment, Observer { event ->
                    event.getContentIfNotHandled()?.let { mark -> snap_edit_view.mark = mark }
                })
                it.snapList.observe(this@SnapFragment, Observer { snapList ->
                    Timber.i("Observe Snap List: ${snapList.size}")
                    snapListAdapter.submitList(snapList)
                })
            }
        }
        setHasOptionsMenu(true)
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

        // Media Scanner
        mediaScanner =
            MediaScannerConnection(view.context, object : MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {
                }

                override fun onScanCompleted(path: String?, uri: Uri?) {
                }
            }).apply { connect() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupListAdapter()
        parseArguments()
    }

    private fun parseArguments() {
        arguments?.let {
            when (it.getString(EXTRA_TYPE)) {
                EXTRA_TYPE_MARK -> viewDataBinding.viewmodel?.initWithMarkId(it.getInt(EXTRA_ID))
                EXTRA_TYPE_PRESET -> viewDataBinding.viewmodel?.initWithPresetId(it.getInt(EXTRA_ID))
                else -> Timber.w("No exists argument for launch SnapFragment")
            }
        }
    }

    override fun onDestroyView() {
        if (mediaScanner.isConnected) mediaScanner.disconnect()
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_MULTI_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        Timber.d("onActivityResult: ${data.data} or ${data.clipData}")
                        data.data?.let { viewDataBinding.viewmodel?.addSnap(Snap(it)) }
                        data.clipData?.let { viewDataBinding.viewmodel?.addSnap(*getSnapsFromClipData(it).toTypedArray()) }
                    }
                    viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupListAdapter() {
        snapListAdapter = SnapListAdapter(object : SnapActionListener {
            override fun onClick(action: Action, snap: Snap) {
                when (action) {
                    Action.ACTION_IMAGE_TARGET_SELECT -> {
                        Timber.i("action select: $snap")
                        snap_edit_view.snap = snap
                    }
                    Action.ACTION_IMAGE_TARGET_DELETE -> {
                        Timber.i("action delete: $snap")
                        viewDataBinding.viewmodel?.removeSnap(snap)
                    }
                }
            }
        })
        viewDataBinding.viewmodel?.let {
            viewDataBinding.recyclerView.adapter = snapListAdapter
            viewDataBinding.recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.snap_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_image_from_gallery -> openGallery()
            R.id.save_snap_image -> saveSnapImage()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun getSnapsFromClipData(clipData: ClipData) =
        mutableListOf<Snap>().apply {
            for (index in 0 until clipData.itemCount)
                add(Snap(clipData.getItemAt(index).uri))
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

    private fun saveSnapImage() {
        if (checkPermission()) {
            val date = Date(System.currentTimeMillis()).let {
                SimpleDateFormat.getDateTimeInstance().format(it)
            }
            val saveImageRootDir = File(SAVE_IMAGE_ROOT_PATH)
            if (!saveImageRootDir.exists()) saveImageRootDir.mkdirs()

            viewDataBinding.viewmodel?.saveSnapImage(
                Completable.fromCallable {
                    val saveImagePath = "$SAVE_IMAGE_ROOT_PATH/Snap_$date.png"
                    snap_edit_view.save(saveImagePath)
                    mediaScanner.scanFile(saveImagePath, SNAP_MEDIA_TYPE)
                }
            )
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
                    saveSnapImage()
                }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showPermissionRationaleDialog(activity: Activity, required: String) {
        val buttonHandler = DialogInterface.OnClickListener { dialog, _ ->
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
        const val REQUEST_PICK_MULTI_IMAGE = 200
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 100

        private const val SNAP_MEDIA_TYPE = "image/png"
        private val SAVE_IMAGE_ROOT_PATH: String =
            getExternalStorageDirectory().absolutePath + File.separator + DIRECTORY_PICTURES + File.separator + "SnapMark"

        fun newInstance() = SnapFragment()
    }

}
