package com.r.cohen.askwiki.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("customBackColor")
fun View.setCustomBackgroundColor(color: Int) {
    this.setBackgroundColor(color)
}

@BindingAdapter("visibleGone")
fun View.setVisibleGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}