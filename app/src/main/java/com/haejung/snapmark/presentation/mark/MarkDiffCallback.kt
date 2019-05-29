package com.haejung.snapmark.presentation.mark

import androidx.recyclerview.widget.DiffUtil
import com.haejung.snapmark.data.Mark

class MarkDiffCallback(
    private val oldList: List<Mark>,
    private val newList: List<Mark>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
                && oldList[oldItemPosition].image.sameAs(newList[newItemPosition].image)
    }

}