package com.haejung.snapmark.presentation.snap

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object SnapListBindings {

    @BindingAdapter("viewModel")
    @JvmStatic
    fun setAdapterWithViewModel(recyclerView: RecyclerView, viewModel: SnapViewModel?) {
        viewModel?.let {
            recyclerView.adapter = SnapListAdapter(it)
        }
    }

    @BindingAdapter("items")
    @JvmStatic
    fun setSnapList(recyclerView: RecyclerView, snapList: List<Snap>?) {
        snapList?.let {
            (recyclerView.adapter as? SnapListAdapter)?.submitList(snapList)
        }
    }

}