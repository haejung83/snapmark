package com.haejung.snapmark.presentation.addmark

interface AddMarkActionListener {
    enum class Action {
        ACTION_SELECT_IMAGE,
        ACTION_SAVE
    }

    fun onClick(action: Action?)
}