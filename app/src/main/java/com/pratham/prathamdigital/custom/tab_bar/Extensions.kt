package com.pratham.prathamdigital.custom.tab_bar

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat

internal fun Context.color(colorResId: Int) = ContextCompat.getColor(this, colorResId)

internal inline fun on21orAbove(up: () -> Unit, down: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        up()
    } else {
        down()
    }
}