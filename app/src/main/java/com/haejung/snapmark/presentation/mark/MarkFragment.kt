package com.haejung.snapmark.presentation.mark

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.databinding.MarkFragmentBinding
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.setupSnackbar
import com.haejung.snapmark.presentation.Event
import com.haejung.snapmark.presentation.addmark.AddMarkActivity
import com.haejung.snapmark.presentation.base.DataBindingNavigationFragment
import com.haejung.snapmark.presentation.snap.SnapActivity
import timber.log.Timber

class MarkFragment private constructor() : DataBindingNavigationFragment<MarkFragmentBinding>() {

    override val layoutResId: Int
        get() = R.layout.mark_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel = (activity as AppCompatActivity).obtainViewModel(MarkViewModel::class.java).also {
            // New Mark
            it.newMarkEvent.observe(this@MarkFragment, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let { openNewMark() }
            })
            // Snap
            it.snapEvent.observe(this@MarkFragment, Observer<Event<Int>> { event ->
                event.getContentIfNotHandled()?.let { markId -> openSnap(markId) }
            })
            // Create Preset
            it.createPresetEvent.observe(this@MarkFragment, Observer<Event<Int>> { event ->
                event.getContentIfNotHandled()?.let { markId -> openCreatePreset(markId) }
            })
            // Show mark popup menu
            it.showMarkPopupMenuEvent.observe(this@MarkFragment, Observer<Event<Pair<View, Mark>>> { event ->
                event.getContentIfNotHandled()?.let { viewMark -> showPopupMenu(viewMark.first, viewMark.second) }
            })
            // Snackbar
            view?.setupSnackbar(this@MarkFragment, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }
        loadMarks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewDataBinding.viewmodel?.handleActivityResult(requestCode, resultCode)
    }

    override fun onNavigationSelected() {
        isAdded
        takeFabOwnership()
    }

    private fun takeFabOwnership() {
        Timber.i("takeFabOwnership")
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_mark)?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(R.drawable.ic_add_black_24dp)
            it.setOnClickListener {
                viewDataBinding.viewmodel?.addMark()
            }
        }
    }

    private fun loadMarks() {
        viewDataBinding.viewmodel?.start()
    }

    private fun openNewMark() {
        startActivity(Intent(context, AddMarkActivity::class.java))
    }

    private fun openSnap(markId: Int) {
        startActivity(Intent(context, SnapActivity::class.java).apply {
            putExtras(SnapActivity.createBundleWithMark(markId))
        })
    }

    private fun openCreatePreset(markId: Int) {
        // TODO: Not implemented yet
        Timber.d("Create Preset with $markId")
    }

    private fun showPopupMenu(anchor: View, mark: Mark) {
        PopupMenu(anchor.context, anchor).apply {
            menuInflater.inflate(R.menu.popup_menu_mark_item, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_remove -> showRemoveConfirmDialog(anchor.context, mark)
                    R.id.action_create_preset -> viewDataBinding.viewmodel?.createPreset(mark)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
        }.show()
    }

    private fun showRemoveConfirmDialog(context: Context, mark: Mark) {
        val buttonHandler = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> viewDataBinding.viewmodel?.removeMark(mark)
            }
            dialog.dismiss()
        }
        MaterialAlertDialogBuilder(context, R.style.Widget_Shrine_MaterialAlertDialog).apply {
            setTitle(R.string.title_dialog_title_removed)
            setMessage(R.string.title_dialog_msg_removed)
            setPositiveButton(R.string.title_ok, buttonHandler)
            setNegativeButton(R.string.title_cancel, buttonHandler)
        }.show()
    }

    companion object {
        fun newInstance() = MarkFragment()
    }

}
