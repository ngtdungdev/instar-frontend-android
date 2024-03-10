package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

class SlowScroller(context: Context) : Scroller(context, DecelerateInterpolator()) {
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, 100000)
    }
}
