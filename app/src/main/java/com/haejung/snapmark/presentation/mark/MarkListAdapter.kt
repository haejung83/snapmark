package com.haejung.snapmark.presentation.mark

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.haejung.snapmark.R
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.databinding.ViewItemMarkBinding
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class MarkListAdapter(
    private val viewModel: MarkViewModel
) : RecyclerView.Adapter<MarkListAdapter.ViewHolder>() {

    private val items = mutableListOf<Mark>()

    private val itemClickListener = object : MarkActionListener {
        override fun onClick(action: MarkActionListener.Action, mark: Mark, view: View?) {
            when (action) {
                MarkActionListener.Action.ACTION_SNAP -> viewModel.snap(mark)
                MarkActionListener.Action.ACTION_OPEN_MENU -> view?.let { showPopupMenu(it, mark) }
            }
        }
    }

    private fun showPopupMenu(anchor: View, mark: Mark) {
        PopupMenu(anchor.context, anchor).apply {
            menuInflater.inflate(R.menu.popup_menu_mark_item, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_remove -> showRemoveConfirmDialog(anchor.context, mark)
                    R.id.action_create_preset -> viewModel.createPreset(mark)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
        }.show()
    }

    private fun showRemoveConfirmDialog(context: Context, mark: Mark) {
        val buttonHandler = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> viewModel.removeMark(mark)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ViewItemMarkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener)
    }

    @SuppressLint("CheckResult")
    fun updateMarkItems(marks: List<Mark>) {
        Single.fromCallable { DiffUtil.calculateDiff(MarkDiffCallback(items, marks)) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                items.clear()
                items.addAll(marks)
                it.dispatchUpdatesTo(this)
            }, { Timber.e(it) })
    }

    class ViewHolder(
        private val binding: ViewItemMarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mark: Mark, actionListener: MarkActionListener) {
            binding.let {
                it.mark = mark
                it.actionListener = actionListener
                it.executePendingBindings()
            }
        }
    }

}