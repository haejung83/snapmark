package com.haejung.snapmark.presentation.snap

interface SnapActionListener {
    enum class Action {
        ACTION_IMAGE_TARGET_SELECT,
        ACTION_IMAGE_TARGET_DELETE
    }

    fun onClick(action: Action, snap: Snap)
}