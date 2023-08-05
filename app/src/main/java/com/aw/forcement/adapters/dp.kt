import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DotsIndicatorDecoration : RecyclerView.ItemDecoration() {

    private val indicatorHeight = 16
    private val indicatorItemPadding = 4
    private val radius = 4f
    private val inactivePaint = Paint()
    private val activePaint = Paint()

    init {
// Set the paint color for the inactive and active indicators
        inactivePaint.color = Color.GRAY
        activePaint.color = Color.WHITE
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
// Use 16dp as an example, you can adjust it according to your preference
        outRect.bottom = indicatorHeight
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

// Get the total number of items in the adapter
        val itemCount = parent.adapter?.itemCount ?: 0

// Loop through each item in the adapter
        for (i in 0 until itemCount) {
// Get a reference to the current item's view holder and view
            val viewHolder = parent.findViewHolderForAdapterPosition(i)
            val view = viewHolder?.itemView ?: continue

// Get the width and height of the item
            val width = view.width
            val height = view.height

// Calculate the total width of the indicators
            val indicatorTotalWidth = (radius * 2 * itemCount) + (indicatorItemPadding * (itemCount - 1))

// Calculate the left and right positions of the indicators relative to the item
            val indicatorStartX = (width - indicatorTotalWidth) / 2f + view.left
            val indicatorPosY = height - indicatorHeight / 2f + view.top

// Draw a dot or circle for each item in the adapter
            for (j in 0 until itemCount) {
// Calculate the position of the current dot or circle relative to the item
                val dx1 = indicatorStartX + (j * (2 * radius + indicatorItemPadding))
                val dx2 = dx1 + 2 * radius

// Check if the current item is active or not
                if (i == j) {
// If active, draw a filled circle with the active paint
                    c.drawCircle((dx1 + dx2) / 2f, indicatorPosY, radius, activePaint)
                } else {
// If inactive, draw a hollow circle with the inactive paint
                    c.drawCircle((dx1 + dx2) / 2f, indicatorPosY, radius, inactivePaint)
                }
            }
        }
    }
}

