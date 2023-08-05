package com.aw.forcement.adapters

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class LoopingSnapHelper : LinearSnapHelper() {

override fun findTargetSnapPosition(
    layoutManager: RecyclerView.LayoutManager,
    velocityX: Int,
    velocityY: Int
): Int {
val targetPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
val firstPosition = 0
val lastPosition = layoutManager.itemCount - 1
return if (targetPosition == lastPosition) {
// If the target position is the last position, return the first position
firstPosition
} else {
// Otherwise, return the target position as usual
targetPosition
}
}
}