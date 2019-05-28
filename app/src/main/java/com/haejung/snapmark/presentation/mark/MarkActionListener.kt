package com.haejung.snapmark.presentation.mark

import android.view.View
import com.haejung.snapmark.data.Mark

interface MarkActionListener {
    enum class Action {
        ACTION_SNAP,
        ACTION_OPEN_MENU
    }

    fun onClick(action: Action, mark: Mark, view: View?)
}