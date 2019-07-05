package com.haejung.snapmark.presentation.mark

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haejung.snapmark.data.Mark

object MarkListBindings {

    @BindingAdapter("items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<Mark>) {
        with(recyclerView.adapter as MarkListAdapter) {
            submitList(items)
        }
    }

}