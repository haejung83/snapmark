package com.haejung.snapmark.presentation.snap.snapedit

import androidx.databinding.BindingAdapter
import com.haejung.snapmark.data.Mark
import com.haejung.snapmark.presentation.snap.Snap

object SnapEditBindings {

    @BindingAdapter("snap")
    @JvmStatic
    fun setCurrentSnap(snapEditView: SnapEditView, snap: Snap?) {
        snap?.let {
            snapEditView.snap = snap
        }
    }

    @BindingAdapter("mark")
    @JvmStatic
    fun setCurrentMark(snapEditView: SnapEditView, mark: Mark?) {
        mark?.let {
            snapEditView.mark = mark
        }
    }

}