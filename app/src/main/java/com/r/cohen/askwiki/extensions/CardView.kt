package com.r.cohen.askwiki.extensions

import android.view.MotionEvent
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter

@BindingAdapter("cardTouchDown", "cardTouchUp", requireAll = true)
fun CardView.setTouchListeners(onTouchDown: () -> Unit, onTouchUp: () -> Unit) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown()
            MotionEvent.ACTION_UP -> {
                performClick()
                onTouchUp()
            }
        }
        true
    }
}

@BindingAdapter("customCardElevation")
fun CardView.setCustomElevation(elevation: Float) {
    this.cardElevation = elevation
}