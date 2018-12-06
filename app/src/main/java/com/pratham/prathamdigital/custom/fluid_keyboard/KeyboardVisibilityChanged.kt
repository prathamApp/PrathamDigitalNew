package com.pratham.prathamdigital.custom.fluid_keyboard

data class KeyboardVisibilityChanged(
        val visible: Boolean,
        val contentHeight: Int,
        val contentHeightBeforeResize: Int
)
