package com.haejung.snapmark.presentation.mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.databinding.ViewItemMarkBinding


class MarkListAdapter(
    private val viewModel: MarkViewModel
) : ListAdapter<Mark, MarkListAdapter.ViewHolder>(MarkItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ViewItemMarkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel.markActionListener)
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

    class MarkItemDiffCallback : DiffUtil.ItemCallback<Mark>() {
        override fun areItemsTheSame(oldItem: Mark, newItem: Mark) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Mark, newItem: Mark) =
            oldItem.name == newItem.name && oldItem.image.sameAs(newItem.image)
    }

}