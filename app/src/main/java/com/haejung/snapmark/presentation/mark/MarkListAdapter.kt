package com.haejung.snapmark.presentation.mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.databinding.ViewItemMarkBinding
import timber.log.Timber
import kotlin.properties.Delegates

class MarkListAdapter(
    private val viewModel: MarkViewModel
) : RecyclerView.Adapter<MarkListAdapter.ViewHolder>() {

    var items: List<Mark> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    private val itemClickListener = object : MarkFragment.MarkActionListener {
        override fun onClick(mark: Mark, action: MarkFragment.MarkActionListener.Action?) {
            Timber.d("OnClick: ${mark.id} - $action")
            when (action) {
                MarkFragment.MarkActionListener.Action.ACTION_SNAP -> viewModel.snap(mark)
                MarkFragment.MarkActionListener.Action.ACTION_OPEN_MENU -> viewModel.showMenu(mark)
            }
        }
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

    class ViewHolder(
        private val binding: ViewItemMarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mark: Mark, actionListener: MarkFragment.MarkActionListener) {
            binding.let {
                it.mark = mark
                it.actionListener = actionListener
                it.executePendingBindings()
            }
        }
    }

}