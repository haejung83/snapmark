package com.haejung.snapmark.presentation.snap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haejung.snapmark.databinding.ViewItemSnapBinding

class SnapListAdapter(
    private val snapViewModel: SnapViewModel
) : ListAdapter<Snap, SnapListAdapter.ViewHolder>(SnapDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ViewItemSnapBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), snapViewModel.snapActionListener)

    class ViewHolder(
        private val binding: ViewItemSnapBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(snap: Snap, actionListener: SnapActionListener) {
            binding.let {
                it.snap = snap
                it.actionListener = actionListener
                it.executePendingBindings()
            }
        }
    }

    class SnapDiffCallback : DiffUtil.ItemCallback<Snap>() {
        override fun areItemsTheSame(oldItem: Snap, newItem: Snap) =
            oldItem.targetImage == newItem.targetImage

        override fun areContentsTheSame(oldItem: Snap, newItem: Snap) =
            oldItem == newItem
    }

}